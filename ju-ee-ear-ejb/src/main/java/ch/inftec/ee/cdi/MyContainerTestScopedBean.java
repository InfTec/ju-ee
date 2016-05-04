package ch.inftec.ee.cdi;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import ch.inftec.ju.ee.test.ContainerTestScoped;

/**
 * Test bean annotated with @ContainerTestScoped used in ContainerTestScopeIT.
 * 
 * @author Martin Meyer <martin.meyer@inftec.ch>
 * 
 */
@ContainerTestScoped
public class MyContainerTestScopedBean {
	private String name = "initial";
	private int nameChanges = 0;

	private static int initializations = 0;
	private static int destructions = 0;

	@PostConstruct
	private void initialize() {
		initializations++;
	}

	@PreDestroy
	private void destroy() {
		destructions++;
	}

	public static int getInititializations() {
		return initializations;
	}

	public static int getDestructions() {
		return destructions;
	}

	public void setName(String name) {
		this.name = name;
		this.nameChanges++;
	}

	public String getName() {
		return name;
	}

	public int getNameChanges() {
		return nameChanges;
	}
}
