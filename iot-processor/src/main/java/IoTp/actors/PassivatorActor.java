package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.model.SensorDataList;
import IoTp.actors.messageTypes.TerminationMessage;
import IoTp.services.DataAggregationService;
import IoTp.services.SensorDataPersistenceService;
import akka.actor.AbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ActorComponent
public class PassivatorActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(PassivatorActor.class);

    private final SensorDataPersistenceService sensorDataPersistenceService;
    private final DataAggregationService dataAggregationService;

    public PassivatorActor(SensorDataPersistenceService sensorDataPersistenceService, DataAggregationService dataAggregationService) {
        this.sensorDataPersistenceService = sensorDataPersistenceService;
        this.dataAggregationService = dataAggregationService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorDataList.class, sensorDataList -> {
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
