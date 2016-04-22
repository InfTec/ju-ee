package ch.inftec.ju.ee.cdi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Target;

/**
 * Annotation to tag an implementation that can be dynamically looked up using CDI.
 * <p>
 * Can be used to determine implementations at runtime based on the server configuration
 * and the tag name.
 * <p>
 * We could also have used the @Named annotation, but then we would have to declare it on the
 * default instance as well.
 * <p>
 * Note that in order to be found, any implementation tagged with DynamicCdiTag needs to implement
 * the tag interface {@link DynamicCdi} as well.
 * @author Martin Meyer <martin.meyer@inftec.ch>
 *
 */
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicCdiTag {
	String value() default "-";
}
