package ch.inftec.ju.ee.cdi;

import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Helper qualifier that can be used to control scopes explicitly depending on certain conditions.
 * <p>
 * We can use it with @Named to inject instances of a bean with different scope and use the qualifier to avoid having to use @Named for
 * <strong>all</strong> injections (even the default one). For an example, see ContainerTestScopeController.
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
@Qualifier
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
public @interface ScopeControl {
}
