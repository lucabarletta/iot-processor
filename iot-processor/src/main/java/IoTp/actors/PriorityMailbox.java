package IoTp.actors;

import akka.actor.ActorSystem;
import akka.dispatch.PriorityGenerator;
import akka.dispatch.UnboundedPriorityMailbox;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriorityMailbox extends UnboundedPriorityMailbox {
    private static final Logger Log = LoggerFactory.getLogger(ManagerActor.class);

    public PriorityMailbox(ActorSystem.Settings settings, Config config) {
        super(new PriorityGenerator() {
            @Override
            public int gen(Object msg) {
                if (msg instanceof ChildActorTerminationMessage) {
                    // has to be high priority due to actor registry in manager actor
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }
}