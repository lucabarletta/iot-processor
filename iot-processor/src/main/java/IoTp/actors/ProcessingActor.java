package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.model.SensorData;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

@ActorComponent
public class ProcessingActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ProcessingActor.class);

    public ProcessingActor() {
        getContext().setReceiveTimeout(Duration.create(5, TimeUnit.SECONDS));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorData.class, data -> {
                            // TODO: implement logic for processing actors (state aggregation)
                            Log.info("received data: " + data + " context:" + context().self().path().name());
                        }
                )
                .match(ReceiveTimeout.class, msg -> onReceiveTimeout())
                .build();
    }

    private void onReceiveTimeout() {
        context().parent().tell("first", ActorRef.noSender());
        context().parent().tell(new ChildActorTerminationMessage(self()), ActorRef.noSender());

        getContext().stop(getSelf());
    }
}
