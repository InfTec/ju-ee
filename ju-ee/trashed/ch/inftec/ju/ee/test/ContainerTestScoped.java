package ch.inftec.ju.ee.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.NormalScope;

/**
 * Custom scope that complies to the lifecycle of a container test (or more precise: test method).
 * 
 * @author martin.meyer@inftec.ch
 * 
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@NormalScope
public @interface ContainerTestScoped {
}
