package IoTp.actors;

import IoTp.config.akkaSpring.ActorComponent;
import akka.actor.AbstractActor;
import akka.actor.DeadLetter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ActorComponent
public class DeadLetterActor extends AbstractActor {
    private final MeterRegistry meterRegistry;

    public DeadLetterActor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DeadLetter.class, this::handleDeadLetter)
                .build();
    }

    private void handleDeadLetter(DeadLetter deadLetter) {
        meterRegistry.counter("DeadLetter_Count").increment();
    }
}
