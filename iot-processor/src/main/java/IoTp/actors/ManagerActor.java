package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.actors.messageTypes.TerminationMessage;
import IoTp.config.akkaSpring.SpringAkkaExtension;
import IoTp.model.SensorData;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ActorComponent
public class ManagerActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ManagerActor.class);

    private final Map<String, ActorRef> processingActorRegistry = new ConcurrentHashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorData.class, data -> {
                    // NOTE: ack, important to only dequeue amount of message the actor system can process (backpressure)
                    context().sender().tell("ack", self());
                    processingActorRegistry.computeIfAbsent(data.getSensorId(), id -> {
                                Log.info("create new processing actor for sensorId: " + data.getSensorId());
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

