package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.config.akkaSpring.SpringAkkaExtension;
import IoTp.actors.messageTypes.TerminationMessage;
import IoTp.model.SensorData;
import IoTp.model.SensorDataList;
import akka.actor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

@ActorComponent
public class ProcessingActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ProcessingActor.class);
    private final SensorDataList sensorDataList = new SensorDataList();
    ActorRef child;

    public ProcessingActor() {
        getContext().setReceiveTimeout(Duration.create(20, TimeUnit.SECONDS));
        child = getContext().actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(getContext().getSystem()).props(PassivatorActor.class).withMailbox("priority-mailbox"), "passivator_" + context().self().path().name());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorData.class, data -> {
                            // TODO: implement logic for processing actors (state aggregation)
                            Log.info("received data: " + data + " context: " + context().self().path().name());
                            // caching
                            sensorDataList.additem(data);
                            if (sensorDataList.getSize() > 2) {
                                child.tell(new SensorDataList(sensorDataList.getItems()), ActorRef.noSender());
                                sensorDataList.reset();
                            }
                        }
                )
                .match(ReceiveTimeout.class, msg -> onReceiveTimeout())
                .build();
    }

    private void onReceiveTimeout() {
        context().parent().tell(new TerminationMessage(self()), ActorRef.noSender());
        child.tell(new TerminationMessage(context().self()), ActorRef.noSender());
        getContext().stop(getSelf());
    }
}
