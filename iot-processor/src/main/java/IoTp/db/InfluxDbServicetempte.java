package IoTp;

import IoTp.actors.InfluxDBClientBuilder;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Profile("influx")
public class InfluxDbServicetempte {
    private final InfluxDBClientBuilder influxDBClientBuilder;

    public InfluxDbServicetempte(InfluxDBClientBuilder influxDBClientBuilder) {
        this.influxDBClientBuilder = influxDBClientBuilder;
    }

    public void doRun() {
        var influxDBClient = influxDBClientBuilder.getClient();
        //
        // Write data
        //
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        //
        // Write by Data Point
        //
        Point point = Point.measurement("temperature")
                .addTag("location", "west")
                .addField("value", 55D)
                .time(Instant.now().toEpochMilli(), WritePrecision.MS);

        writeApi.writePoint(point);

        //
        // Write by LineProtocol
        //
        writeApi.writeRecord(WritePrecision.NS, "temperature,location=north value=60.0");

        //
        // Write by POJO
        //
        Temperature temperature = new Temperature();
        temperature.location = "south";
        temperature.value = 62D;
        temperature.time = Instant.now();

        writeApi.writeMeasurement(WritePrecision.NS, temperature);

        //
        // Query data
        //
        // String flux = "from(bucket:\"iotp\") |> range(start:0) |> filter(fn: (r) => r[\"_measurement\"] == \"temperature\"))";


        // Typed Flux Query DSL
        var queryApi = influxDBClient.getQueryApi();
        Restrictions restrictions = Restrictions.and(
                Restrictions.measurement().equal("temperature")
        );

        Flux flux = Flux
                .from("iotp")
                .range(-120L, ChronoUnit.MINUTES)
                .filter(restrictions)
                .last();


        List<FluxTable> tables = queryApi.query(flux.toString());
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_value"));
            }
        }

        influxDBClient.close();

    }

    @Measurement(name = "temperature")
    private static class Temperature {

        @Column(tag = true)
        String location;

        @Column
        Double value;

        @Column(timestamp = true)
        Instant time;
    }
}
