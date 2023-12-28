package IoTp.camelQueue;


import IoTp.akka.TestConsumer;
import org.apache.camel.Route;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensorDataRouteBuilder {
    @Bean()
    public RoutesBuilder dataRoutesBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:sensorDataInput")
                        .to("seda:processSensorData");

                from("seda:processSensorData")
                        .bean(TestConsumer.class, "log");
            }
        };
    }
}