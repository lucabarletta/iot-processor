package IoTp.actors;

import IoTp.actors.messageTypes.TerminationMessage;
import IoTp.config.akkaSpring.ActorComponent;
import IoTp.config.akkaSpring.SpringAkkaExtension;
import IoTp.model.SensorData;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.codahale.metrics.MetricRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ActorComponent
public class ManagerActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ManagerActor.class);

    private final Map<String, ActorRef> processingActorRegistry = new ConcurrentHashMap<>();
    private final String ACTOR_COUNT_METRIC_KEY = "processing-actor-count";
    private final MeterRegistry meterRegistry;
    private int message = 0;

    public ManagerActor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorData.class, data -> {
                    // NOTE: ack, important to only dequeue amount of message the actor system can process (backpressure)
                    context().sender().tell("ack", self());
                    //Log.info("context: " + context().self().path().name());
                    Log.debug("context: " + context().self().path());

                    processingActorRegistry.computeIfAbsent("processingActor_" + data.getSensorId(), id -> {
                                Log.info("create new processing actor for sensorId: " + data.getSensorId());
                                meterRegistry.gauge(ACTOR_COUNT_METRIC_KEY, processingActorRegistry.size() + 1);
                                return getContext().actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(getContext().getSystem())
                                        .props(ProcessingActor.class).withMailbox("priority-mailbox"), "processingActor_" + data.getSensorId());
                            }
                    ).tell(data, ActorRef.noSender());
                })
                .match(TerminationMessage.class, action -> {
                    processingActorRegistry.remove(action.targetRef().path().name(), action.targetRef());
                    Log.info("removing ActorRef from registry: " + action.targetRef().path().name() + ". " + processingActorRegistry.size() + " actors in the registry");
                })
                .build();
    }
}
