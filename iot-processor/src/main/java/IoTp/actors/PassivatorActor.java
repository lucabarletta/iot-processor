package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.model.SensorDataList;
import IoTp.actors.messageTypes.TerminationMessage;
import IoTp.services.DataAggregationService;
import IoTp.services.SensorDataPersistenceService;
import akka.actor.AbstractActor;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ActorComponent
public class PassivatorActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(PassivatorActor.class);

    private final SensorDataPersistenceService sensorDataPersistenceService;
    private final DataAggregationService dataAggregationService;
    private final MeterRegistry meterRegistry;

    public PassivatorActor(SensorDataPersistenceService sensorDataPersistenceService, DataAggregationService dataAggregationService, MeterRegistry meterRegistry) {
        this.sensorDataPersistenceService = sensorDataPersistenceService;
        this.dataAggregationService = dataAggregationService;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorDataList.class, sensorDataList -> {
                    //Log.info("context: " + context().self().path().name());
                    Log.debug("context: " + context().self().path());

                    var aggregated = dataAggregationService.aggregate(sensorDataList);
                    sensorDataPersistenceService.persist(sensorDataList, aggregated);
                })
                .match(TerminationMessage.class, terminationMessage -> {
                    Log.info("terminating passivator actor " + context().self().path().name() + " from parent: " + context().parent().path().name());
                    getContext().stop(getSelf());
                })
                .build();
    }
}
