package IoTp.db;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class InfluxDBClientBuilder {
    @Value("${spring.iotProcessor.influxdb.url}")
    private String url;

    @Value("${spring.iotProcessor.influxdb.token}")
    private String token;

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
