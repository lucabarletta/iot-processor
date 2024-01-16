package IoTp.controller;

import IoTp.db.InfluxDBClientBuilder;
import IoTp.model.SensorData;
import com.influxdb.client.domain.WritePrecision;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class Testcontroller {
    private final InfluxDBClientBuilder influxDBClientBuilder;

    public Testcontroller(InfluxDBClientBuilder influxDBClientBuilder) {
        this.influxDBClientBuilder = influxDBClientBuilder;
    }

    @RequestMapping("/test")
    public void hello() {
        var api = influxDBClientBuilder.getClient().getWriteApiBlocking();
        var data = new SensorData();
        data.setValue(123.03);
        data.setTime(Instant.now());
        data.setSensorId("sensor1");
        api.writeMeasurement(WritePrecision.MS, data
        );
    }
}
