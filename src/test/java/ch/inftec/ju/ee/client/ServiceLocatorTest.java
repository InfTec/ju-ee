package ch.inftec.ju.ee.client;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;
import java.util.List;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.inftec.ju.ee.client.ModifierTestProducer.TestObject;
import ch.inftec.ju.util.JuUrl;

/**
 * Run ServiceLocator tests in an embedded Weld container.
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 *
 */
@RunWith(Arquillian.class)
public class ServiceLocatorTest {
	@Deployment
    public static JavaArchive createDeployment() {
		// Get a beans.xml with an alternatives declaration for AltAlternative
		URL beansXml = JuUrl.existingResourceRelativeTo("beans.xml", ServiceLocatorTest.class);

        return ShrinkWrap.create(JavaArchive.class)
            .addClass(ModifierTestProducer.class)
			.addAsManifestResource(beansXml, "beans.xml");
    }
	
	@Inject
	private BeanManager beanManager;
	
	@Inject
	@Named("named")
	private TestObject namedObject;
	
	@Test
	public void canInject_namedObject() {
		Assert.assertEquals("named", this.namedObject.getValue());
	}
	
	@Test
	public void canLookup_namedObject() {
		Assert.assertEquals("named", local().cdiNamed(TestObject.class, "named").getValue());
	}

	@Test
	public void canLookup_complexBeans() {
		TestObject to = local().cdiComplex(TestObject.class)
				.named("namedScope")
				.scopeControl()
				.find().one();
		
		Assert.assertEquals("namedWithScopeControl", to.getValue());
	}
	
	@Test
	public void canLookup_complexBeans_scopeControlAnnotated() {
		int cnt = local().cdiComplex(TestObject.class)
				.scopeControl()
				.find().all().size();
		
		Assert.assertEquals(2, cnt);
	}
	
	public interface Alt {
		String getVal();
	}

	public static class AltDefault implements Alt {
		@Override
		public String getVal() {
			return "AltDefault";
		}
	}

	@Alternative
	public static class AltAlternative implements Alt {
		@Override
		public String getVal() {
			return "AltAlternative";
		}
	}

	@Test
	public void cdi_resolvesAlternateBean() {
		Alt alt = local().cdi(Alt.class);
		Assert.assertEquals("AltAlternative", alt.getVal());
	}

	@Test
	public void cdi_withoutMatch_returnsNull() {
		Assert.assertNull(local().cdi(String.class));
	}

	@Test
	public void cdiAll_includesAlternateBeans() {
		List<Alt> alts = local().cdiAll(Alt.class);
		Assert.assertEquals(2, alts.size());
	}

	public interface Multi {
	}

	public static class Multi1 implements Multi {
	}

	public static class Multi2 implements Multi {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Qualifier
	public @interface Special {
	}

	@SuppressWarnings("all")
	static class SpecialAnno implements Annotation, Special {
		@Override
		public Class<? extends Annotation> annotationType() {
			return Special.class;
		}
	}
	
	@Special
	public static class Multi3 implements Multi {
	}

	@Test
	public void cdiAll_resolvesMultipleBeans_withoutQualifier() {
		List<Multi> multis = local().cdiAll(Multi.class);
		// This will return all instance with default qualifier, excluding the @Special instance
		Assert.assertEquals(2, multis.size());
	}

	@Test
	public void cdiAnno_resolvesAnnotatedBean() {
		Multi multi = local().cdiAnno(Multi.class, new SpecialAnno());
		Assert.assertEquals(Multi3.class, multi.getClass());
	}

	@Test
	public void cdiAllAnno_resolvesAll_withAnyQualifier() {
		List<Multi> multis = local().cdiAllAnno(Multi.class, ServiceLocator.ANY);
		Assert.assertEquals(3, multis.size());
	}

	@Test
	public void cdiAllAnno_resolvesAnnotatedBean() {
		List<Multi> multis = local().cdiAllAnno(Multi.class, new SpecialAnno());
		Assert.assertEquals(1, multis.size());
		Assert.assertEquals(Multi3.class, multis.get(0).getClass());
	}
	
	private ServiceLocator local() {
		return ServiceLocatorBuilder.createLocalByBeanManager(this.beanManager);
	}
}
