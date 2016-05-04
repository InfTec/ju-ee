package ch.inftec.ee;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;

import ch.inftec.ju.db.JuConnUtils;
import ch.inftec.ju.testing.db.DbDataUtil;
import ch.inftec.ju.testing.db.DbSchemaUtil;

/**
 * Helper base to update Schema by Liquibaes.
 * <p>
 * Liquibase calls transaction methods commit and rollback, therefore the
 * transaction of the EntityManager must not be managed.
 * @author Martin
 *
 */
@TransactionManagement(TransactionManagementType.BEAN)
@Stateless
public class BeanManagedDataBean {
	@Inject
	private Logger logger;
	
//	@Resource
//	private UserTransaction userTx;
	
	@Resource(lookup="java:jboss/datasources/ee-earDS")
	private DataSource dataSource;
	
//	@PersistenceContext
//	private EntityManager em;
	
//	@Inject
//	private EntityManager em;
	
	public void loadTestData() {
		logger.info("Loading test data");
		logger.debug("DataSource: " + dataSource);
		
		DbSchemaUtil su = new DbSchemaUtil(JuConnUtils.createByDataSource(this.dataSource));
		su.prepareDefaultSchemaAndTestData();
		
		// Load registrant schema and data
		su.runLiquibaseChangeLog("ee-ear-ejb/db/registrant-changeLog.xml");

		DbDataUtil du = new DbDataUtil(JuConnUtils.createByDataSource(this.dataSource));
		du.buildImport().from("/ee-ear-ejb/db/registrant-testData.xml").executeCleanInsert();
		
		logger.info("Test data loaded");
		
//		try (TxHandler tx = new TxHandler(userTx)) {
//			DbSchemaUtil su = new DbSchemaUtil(JuConnUtils.createByDataSource(this.dataSource));
//			su.prepareDefaultSchemaAndTestData();
//			
//			// Load registrant schema and data
//			su.runLiquibaseChangeLog("ee-ear-ejb/db/registrant-changeLog.xml");
//
//			tx.begin();
//			DbDataUtil du = new DbDataUtil(em);
//			du.buildImport().from("/ee-ear-ejb/db/registrant-testData.xml").executeCleanInsert();
//			tx.commit();
//			
//			logger.info("Test data loaded");
//		} catch (Exception ex) {
//			throw new JuRuntimeException("Couldn't load test data", ex);
//		}
	}
}
