package IoTp.services;

import IoTp.model.SensorData;
import IoTp.model.SensorDataAggregate;
import IoTp.model.SensorDataList;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class DataAggregationService {

    public DataAggregationService() {
    }

    public Optional<SensorDataAggregate> aggregate(SensorDataList input) {
        if (input.getSize() < 1) {
            return Optional.empty();
        }

        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (var i : input.getItems()) {
            stats.addValue(i.getValue());
        }
        var aggregate = new SensorDataAggregate();
        aggregate.setMax(stats.getMax());
        aggregate.setMean(stats.getMean());
        aggregate.setMin(stats.getMin());
        aggregate.setMedian(stats.getPercentile(50));
        aggregate.setStandardDeviation(stats.getStandardDeviation());
        aggregate.setSensorId(input.getItems().stream().findFirst().orElseGet(SensorData::new).getSensorId());

        aggregate.setTime(Instant.now());
        return Optional.of(aggregate);
    }
}
