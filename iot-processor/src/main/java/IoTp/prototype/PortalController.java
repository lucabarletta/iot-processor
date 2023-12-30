package IoTp.prototype;

import IoTp.config.akkaSpring.AkkaSpringSupport;
import akka.actor.ActorRef;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PortalController extends AkkaSpringSupport {
    private ActorRef greetingActor;

    @PostConstruct
    public void init() {
        greetingActor = actorOf(GreetingActor.class);
    }

    @RequestMapping("/hello/{name}")
    public String hello(@PathVariable(name = "name") String name) {
        greetingActor.tell(name, ActorRef.noSender());
        return "Hello "+name+"! Please check message on Console.";
    }
}
