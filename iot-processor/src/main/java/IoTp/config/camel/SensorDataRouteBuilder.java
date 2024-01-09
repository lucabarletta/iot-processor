package IoTp.config.camel;


import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensorDataRouteBuilder {
    @Bean()
    public RoutesBuilder dataRoutesBuilder(MeterRegistry metricRegistry) {
        metricRegistry.counter("testCounter").increment();

        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:sensorDataInput")
                        .to("micrometer:counter:xyz?increment=1")
                        .to("seda:processSensorData");
                // TODO metrics
                from("seda:processSensorData")
                        .to("micrometer:counter:xyz?decrement=1")
                        .bean(SensorDataConsumer.class, "log");
            }
        };
    }
}
