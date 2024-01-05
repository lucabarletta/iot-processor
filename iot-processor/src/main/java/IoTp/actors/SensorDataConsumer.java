package IoTp.actors;

import IoTp.config.akkaSpring.AkkaSpringSupport;
import IoTp.model.SensorData;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import jakarta.annotation.PostConstruct;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SensorDataConsumer extends AkkaSpringSupport {
    private ActorRef managerActor;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @PostConstruct
    public void init() {
        managerActor = actorOf(ManagerActor.class, "my-priority-mailbox");
    }

    public void log(Exchange message) {
        // TODO check if data is valid (UUID for Actor Ref)
        // TODO completeable Future with timeout for backpressure handling
        tell(message.getMessage().getBody(SensorData.class));
    }

    public void tell(SensorData sensorData) {
        try {
//            this.managerActor.tell(sensorData, ActorRef.noSender());
            Patterns.ask(this.managerActor, sensorData, TIMEOUT).toCompletableFuture().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
