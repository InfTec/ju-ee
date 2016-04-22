package ch.inftec.ju.ee.cdi;


/**
* This is a tag interface that is used by the DynamicCdiLoader class to lookup
 * all eligable factories available on the classpath.
 * <p>
 * The alternative would be to use some kind of classpath scanning tooling, but this way,
 * we can rely entirely on CDI to get the appropriate classes.
 * <p>
 * A DynamicCdiFactory is a class that contains public non-argument methods that are annotated
 * with @DynamicCdiTag. The DynamicCdiLoader will use these methods to create new dynamic CDI beans.
 * <p>
 * Note that DynamicCdiFactory extends DynamicCdi so we can use a single CDI lookup to find all classes
 * related to dynamic CDI lookup.
 * @author Martin Meyer <martin.meyer@inftec.ch>
 *
 */
public interface DynamicCdiFactory extends DynamicCdi {
}
