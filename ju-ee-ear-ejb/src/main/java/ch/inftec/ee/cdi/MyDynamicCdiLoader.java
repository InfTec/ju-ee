package ch.inftec.ee.cdi;

/**
 * Make DynamicCdiLoader available.
 * @author martin.meyer@inftec.ch
 *
 */
public class MyDynamicCdiLoader {// For some reasons, DynamicCdiLoader is found by Weld even though it's in an external dependency... extends DynamicCdiLoader {
}
