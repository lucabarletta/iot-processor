package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import IoTp.model.SensorDataAggregate;
import IoTp.model.SensorDataList;
import IoTp.model.TerminationMessage;
import akka.actor.AbstractActor;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

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
                    DescriptiveStatistics stats = new DescriptiveStatistics();
                    for (var i : sensorDataList.getItems()) {
                        stats.addValue(i.getValue());
                    }
                    var aggregate = new SensorDataAggregate();
                    aggregate.setMax(stats.getMax());
                    aggregate.setMean(stats.getMean());
                    aggregate.setMin(stats.getMin());
                    aggregate.setMedian(stats.getPercentile(50));
                    aggregate.setStandardDeviation(stats.getStandardDeviation());

                    // present check
                    aggregate.setSensorId(sensorDataList.getItems().stream().findFirst().get().getSensorId());
                    aggregate.setTime(Instant.now());

                    influxWriteService.persist(sensorDataList, aggregate);
                    Log.info("persisted aggregate: " + aggregate);
                })
                .match(TerminationMessage.class, terminationMessage -> {
                    Log.info("terminating passivator actor: " + context().self().path().name());
                    getContext().stop(getSelf());
                })
                .build();
    }
}
