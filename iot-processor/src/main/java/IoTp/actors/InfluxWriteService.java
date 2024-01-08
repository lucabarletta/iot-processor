package IoTp.actors;

import IoTp.model.SensorDataAggregate;
import IoTp.model.SensorDataList;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InfluxWriteService {

    private final InfluxDBClientBuilder influxDBClientBuilder;
    private final WriteApi writeApi;
    private final Logger Log = LoggerFactory.getLogger(InfluxWriteService.class);

    public InfluxWriteService(InfluxDBClientBuilder influxDBClientBuilder) {
        this.influxDBClientBuilder = influxDBClientBuilder;

        var writeOptions = WriteOptions.builder()
                .batchSize(3)
                .bufferLimit(6_000_000)
                .flushInterval(3)
                .build();

        writeApi = influxDBClientBuilder.getClient().makeWriteApi(writeOptions);
    }

    public void persist(SensorDataList data, SensorDataAggregate aggregate) {
        for (var d : data.getItems()) {
            writeApi.writeMeasurement(WritePrecision.MS, d);
        }
        writeApi.writeMeasurement(WritePrecision.MS, aggregate);

        Log.info("persisted: " + data.getSize() + " items");
        influxDBClientBuilder.getClient().close();
    }
}
