package IoTp.db;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("influx")
public class InfluxDBClientBuilder {
    @Value("${spring.iotProcessor.influxdb.url}")
    private String url;

    @Value("${spring.iotProcessor.influxdb.username}")
    private String username;

    @Value("${spring.iotProcessor.influxdb.password}")
    private String password;

    @Value("${spring.iotProcessor.influxdb.org}")
    private String org;

    @Value("${spring.iotProcessor.influxdb.bucket}")
    private String bucket;

    public InfluxDBClient getClient() {
        var options = InfluxDBClientOptions.builder()
                .url(url)
                .authenticate(username, password.toCharArray())
                .org(org)
                .bucket(bucket)
                .build();

        return InfluxDBClientFactory.create(options);
    }
}
