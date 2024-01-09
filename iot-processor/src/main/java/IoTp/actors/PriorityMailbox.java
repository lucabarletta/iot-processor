package IoTp.actors;

import IoTp.actors.messageTypes.TerminationMessage;
import akka.actor.ActorSystem;
import akka.dispatch.PriorityGenerator;
import akka.dispatch.UnboundedPriorityMailbox;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriorityMailbox extends UnboundedPriorityMailbox {

    public PriorityMailbox(ActorSystem.Settings settings, Config config) {
        super(new PriorityGenerator() {
            @Override
            public int gen(Object msg) {
                if (msg instanceof TerminationMessage) {
                    // has to be high priority due to actor registry in manager actor
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }
}