package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.model.TerminationMessage;
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
                    processingActorRegistry.computeIfAbsent(data.getSensorId(), id ->
                            getContext().actorOf(Props.create(ProcessingActor.class), data.getSensorId())).tell(data, ActorRef.noSender());
                })
                .match(TerminationMessage.class, action -> {
                    processingActorRegistry.remove(action.targetRef().path().name(), action.targetRef());
                    Log.warn("removing ActorRef from registry: " + action.targetRef().path().name() + ". " + processingActorRegistry.size() + " left in the registry");
                })
                .build();
    }
}

