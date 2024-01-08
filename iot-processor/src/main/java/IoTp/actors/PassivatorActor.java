package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.model.SensorDataList;
import IoTp.model.TerminationMessage;
import akka.actor.AbstractActor;
import com.influxdb.client.domain.WritePrecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ActorComponent
public class PassivatorActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(PassivatorActor.class);

    private final InfluxWriteService influxWriteService;

    public PassivatorActor(InfluxWriteService influxWriteService) {
        this.influxWriteService = influxWriteService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SensorDataList.class, sensorDataList -> {
                    Log.info("hehe received SensorDataList" + sensorDataList.getItems());
                    influxWriteService.persist(sensorDataList);
//                    writeapi.writeMeasurement("sensordata", "iot-processor", WritePrecision.MS, sensorDataList.getItems());
                })
                .match(TerminationMessage.class, terminationMessage -> {
                    Log.info("terminating passivator actor: " + context().self().path().name());
                    getContext().stop(getSelf());
                })
                .build();
    }
}
