package IoTp.config.camel;

import IoTp.actors.ManagerActor;
import IoTp.config.akkaSpring.AkkaSpringSupport;
import IoTp.config.akkaSpring.SpringAkkaExtension;
import IoTp.model.SensorData;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import jakarta.annotation.PostConstruct;
import org.apache.camel.Exchange;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class SensorDataConsumer extends AkkaSpringSupport {
    private final ActorRef managerActor;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    public SensorDataConsumer(ApplicationContext applicationContext) {
        ActorSystem system = applicationContext.getBean(ActorSystem.class);
        managerActor = system.actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(system).props(ManagerActor.class).withMailbox("priority-mailbox"));
    }

    public void log(Exchange message) {
        // TODO check if data is valid (UUID for Actor Ref)
        tell(message.getMessage().getBody(SensorData.class));
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
