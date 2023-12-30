package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.model.SensorData;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@ActorComponent
public class ManagerActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ManagerActor.class);
    private final Map<String, ActorRef> processingActors = new HashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorData.class, data -> {
                    var actorRef = processingActors.get(data.sensorId());
                    if (actorRef != null) {
                        actorRef.tell(data, context().self());
                        context().sender().tell("ack", self());
                        Log.info("forward data to processing actor: " + actorRef.path().name());
                    } else {
                        final ActorRef child = getContext().actorOf(Props.create(ProcessingActor.class), data.sensorId());
                        processingActors.put(data.sensorId(), child);
                        Log.info("create new processing actor: " + child.path().name());
                        child.tell(data, context().self());
                        // do we need a repsonse here for the completeable future?
                        context().sender().tell("ack", self());
                        Log.info("forward data to  new processing actor: " + child.path().name());
                    }
                }).build();
    }
}

