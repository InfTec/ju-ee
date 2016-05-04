package ch.inftec.ee.cdi;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for CDI lookup.
 * @author Martin Meyer <martin.meyer@inftec.ch>
 *
 */
public class AbstractCdi {
	private static Map<Class<?>, Integer> instanceCnt = new HashMap<>();
	private int callCnt = 0;
	
	public AbstractCdi() {
		if (!AbstractCdi.instanceCnt.containsKey(this.getClass())) {
			AbstractCdi.instanceCnt.put(this.getClass(), 0);
		}
		AbstractCdi.instanceCnt.put(this.getClass(), AbstractCdi.instanceCnt.get(this.getClass()) + 1);
	}
	
	/**
	 * Method that starts with 1 and returns +1 with every call.
	 * @return
	 */
	public int getCalls() {
		return ++this.callCnt;
	}
	
	/**
	 * Method that returns the total count of instances of this specified class.
	 * @return
	 */
	public int getInstanceCnt() {
		return AbstractCdi.instanceCnt.get(this.getClass());
	}
	
	public String type() {
		return "base";
	}
}
