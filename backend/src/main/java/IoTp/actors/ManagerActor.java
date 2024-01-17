package IoTp.actors;

import IoTp.actors.messageTypes.TerminationMessage;
import IoTp.config.akkaSpring.ActorComponent;
import IoTp.config.akkaSpring.SpringAkkaExtension;
import IoTp.model.SensorData;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ActorComponent
public class ManagerActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ManagerActor.class);
    private final Map<String, ActorRef> dispatcherActorRegistry = new ConcurrentHashMap<>();
    private final String ACTOR_COUNT_METRIC_KEY = "dispatcher-actor-count";
    private final MeterRegistry meterRegistry;
    AtomicInteger counter = new AtomicInteger();
    ActorRef nonExistingActor;


    public ManagerActor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        meterRegistry.counter("ManagerActor_started").increment();

        // Dead Letter Simulation - Actor will terminate instantly
        nonExistingActor = getContext().actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(getContext().getSystem())
                .props(DispatcherActor.class), "nonExistingActor");
        getContext().stop(nonExistingActor);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorData.class, data -> {
                    // NOTE: ack, important to only dequeue amount of message the actor system can process (backpressure)
                    context().sender().tell("ack", self());
                    Log.debug("context: " + context().self().path());

                    dispatcherActorRegistry.computeIfAbsent("dispatcherActor_" + data.getSensorId(), id -> {
                                //Log.info("create new DispatcherActor for sensorId: " + data.getSensorId());
                                meterRegistry.gauge(ACTOR_COUNT_METRIC_KEY, dispatcherActorRegistry.size() + 1);
                                return getContext().actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(getContext().getSystem())
                                        .props(DispatcherActor.class).withMailbox("priority-mailbox"), "dispatcherActor_" + data.getSensorId());
                            }
                    ).tell(data, ActorRef.noSender());
                    meterRegistry.gaugeMapSize("actorRegistry_size", List.of(), dispatcherActorRegistry);

                    // To simulate Deadletter
                    counter.getAndIncrement();

                    if (counter.get() > 10000) {
                        nonExistingActor.tell("deadletter", ActorRef.noSender());
                        counter.set(0);
                    }
                })
                .match(TerminationMessage.class, action -> {
                    dispatcherActorRegistry.remove(action.targetRef().path().name(), action.targetRef());
                    meterRegistry.gaugeMapSize("actorRegistry_size", List.of(), dispatcherActorRegistry);
                    //Log.info("removing Dispatcher ActorRef from registry: " + action.targetRef().path().name() + ". " + dispatcherActorRegistry.size() + " actors in the registry");
                })
                .build();
    }
}
