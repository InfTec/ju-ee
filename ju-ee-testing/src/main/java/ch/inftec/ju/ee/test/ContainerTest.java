package ch.inftec.ju.ee.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.junit.Rule;

import ch.inftec.ju.db.JuEmUtil;
import ch.inftec.ju.db.TxHandler;
import ch.inftec.ju.ee.client.ServiceLocator;
import ch.inftec.ju.ee.client.ServiceLocatorBuilder;
import ch.inftec.ju.ee.test.ContainerTestRunnerRule.TestRunnerType;
import ch.inftec.ju.ee.test.TestRunnerFacade.TestRunnerContext;
import ch.inftec.ju.util.JuRuntimeException;

/**
 * Base class for container tests, i.e. integration tests that run in the application
 * server VM.
 * <p>
 * Note that any JUnit related features like @Before, @After and so on will run in the
 * client JVM and not on the server.
 * <p>
 * You can, however, override the doInit method to perform initialization tasks and use
 * the serviceLocator to perform CDI and JNDI lookups in the container.
 * <p>
 * Note that the container will have to provide an EntityManager producer so we can inject it
 * into our test class.
 * <p>
 * The ContainerTest provides a TxHandler instance that can be used to control the transaction if
 * necessary. When entering the test method, we will have a running transaction and it will be automatically
 * committed if necessary after completing the test.
 * <p>
 * Parameterized tests are <strong>not</strong> supported.
 * <p>
 * @author Martin
 */
public abstract class ContainerTest 
		implements TestRunnerFacade.ContextAware
			, TestRunnerFacade.Initializable
			, TestRunnerFacade.TransactionAware {
	
    protected Logger _log = Logger.getLogger(this.getClass());
    
    /**
     * Rule that runs the method statements on the remote JBoss container VM
     */
    @Rule
	public ContainerTestRunnerRule testRunnerRule = new ContainerTestRunnerRule(TestRunnerType.CONTAINER);
    
    private TestRunnerContext context;
    
    /**
     * EntityManager provided by the container.
     */
    protected EntityManager em;
    
    /**
     * JuEmUtil instance wrapped around the EntityManager.
     */
    protected JuEmUtil emUtil;

    /**
     * TxHandler that can be used to control the DB transaction.
     */
    private TxHandler txHandler;
    
    /**
     * ServiceLocator instance that can be used to lookup JNDI or CDI objects on the server.
     * <p>
     * Note that the ServiceLocator is not configured to lookup remote objects
     */
    protected ServiceLocator serviceLocator;
    
	@Override
	public final void setContext(TestRunnerContext context) {
		this.context = context;
	}

	@Override
	public void setTxHandler(TxHandler txHandler) {
		this.txHandler = txHandler;
	}
	
	protected ContainerTestTransactionHandler getTransactionHandler() {
		return new ContainerTestTransactionHandler() {
			
			@Override
			public void rollbackIfNotCommittedWithoutStartingNewTransaction() {
				txHandler.rollbackIfNotCommitted();
			}
			
			@Override
			public void rollbackIfNotCommittedAndStartNewTransaction() {
				txHandler.rollbackIfNotCommitted();
				txHandler.begin();
			}
			
			@Override
			public void commitAndStartNewTransaction() {
				txHandler.commit(true);
			}
		};
	}
	
	@Override
	public final void init() {
		this.serviceLocator = ServiceLocatorBuilder.buildLocal().createServiceLocator();
		this.em = this.serviceLocator.cdi(EntityManager.class);
		this.emUtil = new JuEmUtil(this.em);
		
		this.doInit();
	}

	/**
	 * Extending classes can override this method to perform custom initialization.
	 */
	protected void doInit() {
	}
	
	/**
	 * Initializes the context and txHandler fields from the specified host test.
	 * <p>
	 * Can be used if we want to call a test from another test.
	 * 
	 * @param hostTest
	 *            Test to load settings from
	 */
	protected void initFrom(ContainerTest hostTest) {
		this.context = hostTest.context;
		this.txHandler = hostTest.txHandler;
	}

	/**
	 * Gets a Path instance relative to the 'local' test (rather than the JBoss server context).
	 * 
	 * @param relativePath
	 *            Relative path like <code>target/file.xml</code>
	 * @param createParentDirectories
	 *            If true, the parent directories of the path are created if necessary
	 * @return Path relative to the unit test VM
	 */
	protected final Path getLocalPath(String relativePath, boolean createParentDirectories) {
		return this.getLocalPath(Paths.get(relativePath), createParentDirectories);
	}
	
	/**
	 * Gets a Path instance relative to the 'local' test (rather than the JBoss server context).
	 * @param relativePath Relative path like <code>target/file.xml</code>
	 * @param createParentDirectories If true, the parent directories of the path are created if necessary
	 * @return Path relative to the unit test VM
	 */
	protected final Path getLocalPath(Path relativePath, boolean createParentDirectories) {
		Path localRoot = Paths.get(this.context.getLocalRoot());
		Path localPath = localRoot.resolve(relativePath);
		
		if (createParentDirectories) {
			try {
				Files.createDirectories(localPath.getParent());
			} catch (IOException ex) {
				throw new JuRuntimeException("Couldn't create parent directories", ex);
			}
		}
		
		return localPath;
	}
}
