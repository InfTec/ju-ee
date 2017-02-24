package ch.inftec.ju.ee.test;

/**
 * Helper interface for the Container test to deal with transactions.
 * 
 * @author martin.meyer@inftec.ch
 *
 */
public interface ContainerTestTransactionHandler {
	void rollbackIfNotCommittedAndStartNewTransaction();

	void rollbackIfNotCommittedWithoutStartingNewTransaction();

	void commitAndStartNewTransaction();
}
