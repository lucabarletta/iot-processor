package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.model.SensorData;
import akka.actor.AbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ActorComponent
public class ProcessingActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ProcessingActor.class);
    // TODO set timeout and terminate after

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorData.class, data -> {
                            Log.info("received data: " + data + " context:" + context().self().path().name());
                        }
                )
                .build();
    }
}
