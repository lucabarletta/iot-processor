package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.config.akkaSpring.SpringAkkaExtension;
import IoTp.actors.messageTypes.TerminationMessage;
import IoTp.model.SensorData;
import IoTp.model.SensorDataBatch;
import akka.actor.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import scala.concurrent.duration.Duration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ActorComponent
public class DispatcherActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(DispatcherActor.class);
    private final SensorDataBatch sensorDataBatch = new SensorDataBatch();
    private final MeterRegistry meterRegistry;
    private ActorRef child;

    @Value("${spring.actor-logic.batch-processing-size}")
    private int batchProcessingSize;

    public DispatcherActor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        getContext().setReceiveTimeout(Duration.create(20, TimeUnit.SECONDS));
        meterRegistry.counter("DispatcherActor_started").increment();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorData.class, data -> {
                            Log.debug("context: " + context().self().path());
                            meterRegistry.gauge("sensor_" + data.getSensorId() + "_value", data.getValue());
                            sensorDataBatch.additem(data);
                            if (sensorDataBatch.getSize() > batchProcessingSize) {
                                child = getContext().actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(getContext()
                                                .getSystem())
                                        .props(ProcessingActor.class)
                                        .withMailbox("priority-mailbox"), "processingActor_" + context().self().path().name() + "_" + UUID.randomUUID());
                                child.tell(new SensorDataBatch(sensorDataBatch.getItems()), ActorRef.noSender());
                                sensorDataBatch.reset();
                                // reset ref
                                child = null;
                            }
                        }
                )
                .match(ReceiveTimeout.class, msg -> onReceiveTimeout())
                .build();
    }

    private void onReceiveTimeout() {
        meterRegistry.counter("DispatcherActor_terminated").increment();
        context().parent().tell(new TerminationMessage(self()), ActorRef.noSender());
        getContext().stop(getSelf());
    }
}
