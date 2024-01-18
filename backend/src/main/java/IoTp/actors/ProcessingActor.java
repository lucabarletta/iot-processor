package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.model.SensorDataBatch;
import IoTp.actors.messageTypes.TerminationMessage;
import IoTp.services.AggregationService;
import IoTp.services.PersistenceService;
import akka.actor.AbstractActor;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ActorComponent
public class ProcessingActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ProcessingActor.class);

    private final PersistenceService persistenceService;
    private final AggregationService aggregationService;
    private final MeterRegistry meterRegistry;

    public ProcessingActor(PersistenceService persistenceService, AggregationService aggregationService, MeterRegistry meterRegistry) {
        this.persistenceService = persistenceService;
        this.aggregationService = aggregationService;
        this.meterRegistry = meterRegistry;
        meterRegistry.counter(getClass().getName() + "_started").increment();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorDataBatch.class, sensorDataBatch -> {
                    Log.debug("context: " + context().self().path());
                    var aggregated = aggregationService.aggregate(sensorDataBatch);
                    persistenceService.persist(sensorDataBatch, aggregated);
                    onTerminate();
                })
                .match(TerminationMessage.class, terminationMessage -> {
                    getContext().stop(getSelf());
                })
                .build();
    }

    private void onTerminate() {
        Log.info("ProcessingActor terminate context: " + context().self().path().name());
        meterRegistry.counter(getClass().getName() + "_terminated").increment();
        getContext().stop(getSelf());
    }
}
