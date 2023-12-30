package IoTp.prototype;

import IoTp.config.akkaSpring.ActorComponent;
import akka.actor.AbstractActor;


@ActorComponent
public class GreetingActor extends AbstractActor {
    private final GreetingService greetingService;

    public GreetingActor(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, text -> {
                    System.out.println(greetingService.greet(text));
                })
                .build();
    }

}