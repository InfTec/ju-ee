package ch.inftec.ju.ee.test.junit;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Categories can be included using the failsafe property -Dgroups=ch.inftec.ju.ee.test.junit.CategoryOne[,ch.inftec...]
 * @author Martin Meyer <martin.meyer@inftec.ch>
 *
 */
@Category(CategoryOne.class)
public class JUnitCategoriesIT {
	@Test
	public void categoryOneTest() {
	}

	@Category(CategoryTwo.class)
	@Test
	public void categoryTwoTest() {
	}
	
	@Category(CategoryThree.class)
	@Test
	public void categoryThreeTest() {
	}
}
