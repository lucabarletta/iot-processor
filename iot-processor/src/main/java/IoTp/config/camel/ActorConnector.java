package IoTp.config.camel;

import IoTp.actors.ManagerActor;
import IoTp.config.akkaSpring.AkkaSpringSupport;
import IoTp.config.akkaSpring.SpringAkkaExtension;
import IoTp.model.SensorData;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ActorConnector extends AkkaSpringSupport {
    private final ActorRef managerActor;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final Logger Log = LoggerFactory.getLogger(ActorConnector.class);

    public ActorConnector(ApplicationContext applicationContext) {
        ActorSystem system = applicationContext.getBean(ActorSystem.class);
        managerActor = system.actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(system).props(ManagerActor.class).withMailbox("priority-mailbox"), "managerActor");
    }

    public void log(Exchange message) {
        // TODO check if data is valid (UUID for Actor Ref)
        Log.info("recievied Object" + message);
        tell(message.getMessage().getBody(SensorData.class));
    }

    public void process(SensorData sensorData) {
        tell(sensorData);
    }

    private void tell(SensorData sensorData) {
        try {
            Patterns.ask(this.managerActor, sensorData, TIMEOUT).toCompletableFuture().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}