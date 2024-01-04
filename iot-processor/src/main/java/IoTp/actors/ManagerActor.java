package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
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
                    // NOTE: important to only dequeue amount of message the actor system can handle
                    context().sender().tell("ack", self());
                    processingActorRegistry.computeIfAbsent(data.sensorId(), id ->
                            getContext().actorOf(Props.create(ProcessingActor.class), data.sensorId())).tell(data, ActorRef.noSender());
//                    Log.info("forward data to existing processing actor: " + actorRef.path().name());
//                    } else {
//                        final ActorRef child = getContext().actorOf(Props.create(ProcessingActor.class), data.sensorId());
//                        Log.info("add processing actor to registry: " + child.path().name());
//                        processingActorRegistry.put(data.sensorId(), child);
//                        child.tell(data, context().self());
//                    }
                })
                .match(String.class, s -> {
                    Log.info("should be not first");
                })
                .match(ChildActorTerminationMessage.class, action -> {
                    processingActorRegistry.remove(action.actorRef.path().name(), action.actorRef);
                    Log.warn("removing ActorRef from registry: " + action.actorRef.path().name() + ". " + processingActorRegistry.size() + " left in the registry");
                })
                .build();
    }
}

