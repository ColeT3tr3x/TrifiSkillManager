package facejup.skillpack.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import facejup.skillpack.main.EventManager;
import facejup.skillpack.users.User;

public class ExperienceListener implements Listener {
	
	private EventManager em;
	
	public ExperienceListener(EventManager em)
	{
		this.em = em;
		em.getMain().getServer().getPluginManager().registerEvents(this, em.getMain());
	}
	
	@EventHandler
	public void playerEXPChangeEvent(PlayerExpChangeEvent event)
	{
		Player player = event.getPlayer();
		User user = em.getMain().getUserManager().getUser(player);
		if(event.getAmount() > 0)
		{
			user.incExp(event.getAmount());
		}
		event.setAmount(0);
		player.setLevel(user.getLevel());
		player.setExp(1.0f*user.getLevelProgress() / user.getLevelExp(user.getLevel()));
	}

}
