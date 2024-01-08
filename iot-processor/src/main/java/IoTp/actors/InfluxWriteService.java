package IoTp.actors;

import IoTp.model.SensorData;
import IoTp.model.SensorDataList;
import akka.io.Tcp;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import org.springframework.stereotype.Component;

@Component
public class InfluxWriteService {

    private final InfluxDBClientBuilder influxDBClientBuilder;
    private final WriteApiBlocking writeApiBlocking;


    public InfluxWriteService(InfluxDBClientBuilder influxDBClientBuilder) {
        this.influxDBClientBuilder = influxDBClientBuilder;
        writeApiBlocking = influxDBClientBuilder.getWriteApi();
    }

    public void persist(SensorDataList data) {
        data.getItems().forEach(t -> {
            writeApiBlocking.writeMeasurement(WritePrecision.MS, t);
        });

        influxDBClientBuilder.getClient().close();

    }
}
