package ch.inftec.ee;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import ch.inftec.ju.testing.db.data.entity.Player;
import ch.inftec.ju.testing.db.data.repo.PlayerRepo;

@RequestScoped
@Named
public class PlayerData {
	@Inject
	private PlayerRepo playerRepo;

	@Inject
	private EntityManager em;
	
	@Inject
	private Logger logger;
	
	@Inject
	private PlayerFacade playerFacade;
	
	private String firstPlayerName;
	
	public long getPlayerCountEm() {
//		Player p = em.find(Player.class, 1L);
		return em.createQuery("select count(p) from Player p", Long.class).getSingleResult();
	}

	public long getPlayerCountRepo() {
		return playerRepo.count(); 
	}
	
	public String getTest() {
		return "blabla";
	}
	
	public void setFirstPlayerName(String name) {
		this.firstPlayerName = name;
	}
	
	public String getFirstPlayerName() {
		return this.firstPlayerName;
	}
	
	public void saveFirstPlayerName() {
		this.playerFacade.saveFirstPlayerName(this.getFirstPlayerName());
	}
	
	public String getFirstPlayerNameOnDb() {
		Player p = this.playerRepo.findOne(-1L); 
		return p == null ? null : p.getLastName();
	}
}
