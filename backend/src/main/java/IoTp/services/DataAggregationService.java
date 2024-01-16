package IoTp.services;

import IoTp.model.SensorData;
import IoTp.model.SensorDataAggregate;
import IoTp.model.SensorDataList;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class DataAggregationService {
    private final MeterRegistry meterRegistry;
    private final static Logger Log = LoggerFactory.getLogger(DataAggregationService.class);

    public DataAggregationService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Optional<SensorDataAggregate> aggregate(SensorDataList input) {
        return meterRegistry.timer("DataaggregationService_aggregate_duration").record(() -> {
            if (input.isEmpty()) {
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
            aggregate.setP95(stats.getPercentile(95.00));
            aggregate.setSensorId(input.getItems().stream().findFirst().orElseGet(SensorData::new).getSensorId());
            aggregate.setTime(Instant.now());
            return Optional.of(aggregate);
        });
    }
}
