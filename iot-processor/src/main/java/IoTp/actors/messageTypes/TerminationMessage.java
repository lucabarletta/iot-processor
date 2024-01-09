package IoTp.actors.messageTypes;

import akka.actor.ActorRef;

public record TerminationMessage(ActorRef targetRef) {
}
