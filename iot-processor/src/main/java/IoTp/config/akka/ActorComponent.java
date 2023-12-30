package IoTp.config.akka;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActorComponent {

    /**
     * register actor on cluster or not
     * ClusterClientReceptionist.get(actorSystem).registerService(ref);
     *
     * @return registered indication
     */
    boolean registered() default false;
}
