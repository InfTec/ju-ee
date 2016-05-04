package ch.inftec.ju.ee.cdi;


/**
* This is a tag interface that is used by the DynamicCdiLoader class to lookup
 * all eligable classes available on the classpath.
 * <p>
 * The alternative would be to use some kind of classpath scanning tooling, but this way,
 * we can rely entirely on CDI to get the appropriate classes.
 * 
 * @author martin.meyer@inftec.ch
 *
 */
public interface DynamicCdi {
}
