package IoTp.config.camel;


import IoTp.services.QueueMessageProcessor;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensorDataRabbitMQRouteBuilder {
    @Bean()
    public RoutesBuilder rabbitmqDataRouteBuilder(MeterRegistry metricRegistry) {
        metricRegistry.counter("testCounter").increment();

        return new RouteBuilder() {
            @Override
            public void configure() {
                from("spring-rabbitmq:iot.exchange?queues=iot.queue")
                        .to("micrometer:counter:rabbitmq.inbound?increment=1")
                        .to("seda:rabbitmqSensorData");
                from("seda:rabbitmqSensorData")
                        .to("micrometer:counter:rabbitmq.inbound?decrement=1")
                        .bean(QueueMessageProcessor.class, "consume");
            }
        };
    }
}
