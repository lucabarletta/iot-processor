package IoTp.model;

import akka.actor.ActorRef;

public record TerminationMessage(ActorRef targetRef) {
}
