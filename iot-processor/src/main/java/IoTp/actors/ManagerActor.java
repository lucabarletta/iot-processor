package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.model.SensorData;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@ActorComponent
public class ManagerActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ManagerActor.class);
    private final Map<String, ActorRef> processingActorRegistry = new HashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorData.class, data -> {
                    context().sender().tell("ack", self());
                    var actorRef = processingActorRegistry.get(data.sensorId());
                    if (actorRef != null) {
                        actorRef.tell(data, context().self());
                        Log.info("forward data to existing processing actor: " + actorRef.path().name());
                    } else {
                        final ActorRef child = getContext().actorOf(Props.create(ProcessingActor.class), data.sensorId());
                        Log.info("add processing actor to registry: " + child.path().name());
                        processingActorRegistry.put(data.sensorId(), child);
                        child.tell(data, context().self());
                    }
                })
                .match(RemoveReferenceMessage.class, action -> {
                    processingActorRegistry.remove(action.ref.path().name(), action.ref);
                    Log.warn("removing ActorRef from registry: " + action.ref.path().name() + ". " + processingActorRegistry.size() + " left in the registry");
                })
                .build();
    }
}

