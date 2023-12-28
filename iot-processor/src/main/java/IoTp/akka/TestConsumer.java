package IoTp.akka;

import akka.actor.ActorSystem;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;
import scala.sys.Prop;

@Component
public class TestConsumer {
    public TestConsumer() {
    }

    public void log(Exchange message) {
        Message rawMessage = message.getMessage();
        System.out.println(rawMessage.getBody(String.class));
    }
}
