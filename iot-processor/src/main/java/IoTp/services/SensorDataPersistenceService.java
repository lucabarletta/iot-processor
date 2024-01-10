package IoTp.services;

import IoTp.db.InfluxDBClientBuilder;
import IoTp.model.SensorDataAggregate;
import IoTp.model.SensorDataList;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SensorDataPersistenceService {

    private final InfluxDBClientBuilder influxDBClientBuilder;
    private final WriteApi writeApi;
    private final Logger Log = LoggerFactory.getLogger(SensorDataPersistenceService.class);

    public SensorDataPersistenceService(InfluxDBClientBuilder influxDBClientBuilder) {
        this.influxDBClientBuilder = influxDBClientBuilder;

        writeApi = influxDBClientBuilder.getClient()
                .makeWriteApi(
                        WriteOptions.builder()
                                .batchSize(1000)
                                .bufferLimit(6_000_000)
                                .flushInterval(1000)
                                .build()
                );
    }

    public void persist(SensorDataList data, Optional<SensorDataAggregate> aggregate) {
        aggregate.ifPresent(sensorDataAggregate -> writeApi.writeMeasurement(WritePrecision.MS, sensorDataAggregate));
        for (var d : data.getItems()) {
            writeApi.writeMeasurement(WritePrecision.MS, d);
        }
        Log.info("persisted: " + data.getSize() + " items");
        influxDBClientBuilder.getClient().close();
    }
}
