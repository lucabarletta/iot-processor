package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.config.akkaSpring.SpringAkkaExtension;
import IoTp.actors.messageTypes.TerminationMessage;
import IoTp.model.SensorData;
import IoTp.model.SensorDataList;
import akka.actor.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

@ActorComponent
public class ProcessingActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ProcessingActor.class);
    private final SensorDataList sensorDataList = new SensorDataList();
    private final MeterRegistry meterRegistry;
    private final ActorRef child;

    @Value("${spring.actor-logic.batch-processing-size}")
    private int batchProcessingSize;

    public ProcessingActor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        getContext().setReceiveTimeout(Duration.create(20, TimeUnit.SECONDS));
        child = getContext()
                .actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(getContext()
                                .getSystem())
                        .props(PassivatorActor.class)
                        .withMailbox("priority-mailbox"), "passivatorActor_" + context().self().path().name());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorData.class, data -> {
                            //Log.info("context: " + context().self().path().name());
                            Log.debug("context: " + context().self().path());

                            meterRegistry.gauge("value of Sensor: " + data.getSensorId(), data.getValue());
                            // Log.info("received data: " + data + " context: " + context().self().path().name());
                            // caching for batch processing
                            sensorDataList.additem(data);
                            if (sensorDataList.getSize() > batchProcessingSize) {
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
        //   child.tell(new TerminationMessage(context().self()), ActorRef.noSender());
        getContext().stop(child);
        getContext().stop(getSelf());
    }
}
