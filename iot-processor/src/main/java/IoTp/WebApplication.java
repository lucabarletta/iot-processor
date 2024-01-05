package IoTp;

import kamon.Kamon;
import kamon.prometheus.PrometheusReporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WebApplication {

    public static void main(String[] args) {
        Kamon.init();
        SpringApplication.run(WebApplication.class, args);
    }
}
