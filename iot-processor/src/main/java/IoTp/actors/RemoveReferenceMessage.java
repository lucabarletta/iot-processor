package IoTp.actors;

import akka.actor.ActorRef;

public class RemoveReferenceMessage {
    public ActorRef ref;

    public RemoveReferenceMessage(ActorRef ref) {
        this.ref = ref;
    }
}
