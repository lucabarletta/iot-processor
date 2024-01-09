package IoTp.config.camel;


import IoTp.actors.SensorDataConsumer;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SensorDataRouteBuilder extends RouteBuilder {
    @Value("${mockdata.customerList}")
    private List<String> queueNames = new ArrayList<>();

    //private final List<String> queueNames = List.of("Customer1", "Customer2", "Customer3");
    public SensorDataRouteBuilder() {
    }

    @Override
    public void configure() {
        queueNames.forEach(queueName -> {
            from("direct:sensor" + queueName)
                    .to("seda:processSensor" + queueName);
            // TODO metrics
            from("seda:processSensor" + queueName)
                    .bean(SensorDataConsumer.class, "log");
        });
    }
}
