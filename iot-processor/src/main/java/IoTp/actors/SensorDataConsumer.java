package IoTp.actors;

import IoTp.config.akkaSpring.AkkaSpringSupport;
import IoTp.model.SensorData;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import jakarta.annotation.PostConstruct;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class SensorDataConsumer {
    private ActorRef managerActor;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private final Logger Log = LoggerFactory.getLogger(SensorDataConsumer.class);

    public SensorDataConsumer(ApplicationContext context) {
        ActorSystem actorSystem = context.getBean(ActorSystem.class);
    }

    public void log(Exchange message) {
        // TODO check if data is valid (UUID for Actor Ref)
        // TODO completeable Future with timeout for backpressure handling
        Log.info("received message: " + message.getMessage().getBody(SensorData.class));
        //tell(message.getMessage().getBody(SensorData.class));
    }

    public void tell(SensorData sensorData) {
        sensorData.setTime(Instant.now());
        try {
            Patterns.ask(this.managerActor, sensorData, TIMEOUT).toCompletableFuture().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
