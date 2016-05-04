package ch.inftec.ee;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Startup
@Singleton
public class StartupBean {
	@Inject
	private BeanManagedDataBean beanManagedDataBean;
	
//	@Resource(lookup="java:jboss/datasources/ee-earDS")
//	private DataSource dataSource;
	
	@PostConstruct
	public void onStartup() {
		beanManagedDataBean.loadTestData();
	}
	
	/**
	 * Property returning true to signal that JBoss startup is complete.
	 * <p>
	 * Can be used in a JSF to act as a ping page for Cargo or the like. Otherwise, the
	 * web context will be available before the data loading is really complete.
	 * @return True if the StartupBean initialization is complete
	 */
	public boolean isUp() {
		return true;
	}
}