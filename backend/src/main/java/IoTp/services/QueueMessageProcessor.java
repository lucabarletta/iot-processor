package IoTp.services;

import IoTp.config.camel.ActorConnector;
import org.apache.camel.Exchange;
import org.apache.camel.tooling.model.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueueMessageProcessor {
    private final ActorConnector connector;
    private final SensorDataMapper sensorDataMapper;
    private static final Logger Log = LoggerFactory.getLogger(QueueMessageProcessor.class);

    public QueueMessageProcessor(ActorConnector connector, SensorDataMapper sensorDataMapper) {
        this.connector = connector;
        this.sensorDataMapper = sensorDataMapper;
    }

    public void consume(Exchange exchange) {
        var messageBody = exchange.getIn().getBody(String.class);
        if (!Strings.isNullOrEmpty(messageBody)) {
            var parsed = sensorDataMapper.parse(messageBody);
            parsed.ifPresentOrElse(connector::process,
                    () -> Log.warn("could not parse messageId: " + exchange.getMessage().getMessageId()));
        }
    }
}
