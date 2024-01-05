package IoTp.actors;

import akka.actor.ActorRef;

public record ChildActorTerminationMessage(ActorRef actorRef) {
}
