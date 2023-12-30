package IoTp.akka;

import IoTp.config.akka.ActorComponent;
import IoTp.model.SensorData;
import akka.actor.AbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ActorComponent
public class ManagerActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ManagerActor.class);

    public ManagerActor() {
    }

    @Override
    public Receive createReceive() {
        // TODO create objects for transfer data to actors
        return receiveBuilder().match(SensorData.class, t -> {
                    Log.info("received data: " + t + " context:" +  context().self());
                }
        ).build();
    }
}

