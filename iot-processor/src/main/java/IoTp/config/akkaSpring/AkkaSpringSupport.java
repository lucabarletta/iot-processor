package IoTp.config.akkaSpring;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;


public class AkkaSpringSupport {
    @Autowired
    private ActorSystem actorSystem;

    protected ActorRef actorOf(Class<? extends Actor> actorClass) {
        return actorSystem.actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(actorSystem).props(actorClass), actorClass.getCanonicalName());
    }

    protected ActorSelection actorSelection(String path) {
        return actorSystem.actorSelection(path);
    }
}
