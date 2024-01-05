package IoTp.actors;

import akka.actor.ActorRef;

public class ChildActorTerminationMessage {
    ActorRef actorRef;

    public ChildActorTerminationMessage(ActorRef actorRef) {
        this.actorRef = actorRef;
    }
}
