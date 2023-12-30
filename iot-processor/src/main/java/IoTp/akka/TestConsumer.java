package IoTp.akka;

import IoTp.config.akka.AkkaSpringSupport;
import IoTp.model.SensorData;
import akka.actor.ActorRef;
import jakarta.annotation.PostConstruct;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

@Component
public class TestConsumer extends AkkaSpringSupport {
    private ActorRef managerActor;

    @PostConstruct
    public void init() {
        managerActor = actorOf(ManagerActor.class);
    }

    public TestConsumer() {
    }

    public void log(Exchange message) {
        managerActor.tell(message.getMessage().getBody(SensorData.class), ActorRef.noSender());
        Message rawMessage = message.getMessage();
        System.out.println(rawMessage.getBody(SensorData.class));
    }
}
