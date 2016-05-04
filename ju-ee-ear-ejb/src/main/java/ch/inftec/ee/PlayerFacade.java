package ch.inftec.ee;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import ch.inftec.ju.testing.db.data.repo.PlayerRepo;

@Stateless
public class PlayerFacade {
	@Inject
	private Logger logger;
	
	@Inject
	private PlayerRepo playerRepo;
	
	public void saveFirstPlayerName(String name) {
		logger.info("Saving firstPlayerName " + name);
		this.playerRepo.findOne(-1L).setLastName(name);
	}
}
