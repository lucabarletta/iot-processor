package IoTp.prototype;

import IoTp.config.ActorComponent;
import akka.actor.AbstractActor;
import org.springframework.beans.factory.annotation.Autowired;


@ActorComponent
public class GreetingActor extends AbstractActor {
    @Autowired
    private GreetingService greetingService;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, text -> {
                    System.out.println(greetingService.greet(text));
                })
                .build();
    }

}