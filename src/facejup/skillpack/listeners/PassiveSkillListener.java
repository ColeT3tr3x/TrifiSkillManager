package facejup.skillpack.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.sucy.skill.SkillAPI;

import facejup.skillpack.main.EventManager;
import facejup.skillpack.users.User;

public class PassiveSkillListener implements Listener {

	private EventManager em;

	public PassiveSkillListener(EventManager em)
	{
		this.em = em;
		em.getMain().getServer().getPluginManager().registerEvents(this, em.getMain());
	}

	@EventHandler
	public void entityDamageEvent(EntityDamageEvent event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof Player)
		{
			Player player = (Player) ent;
			User user = em.getMain().getUserManager().getUser(player);
			if((event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION) && user.hasSkill(SkillAPI.getSkill("BlastResistance"), 1))
			{
				int level = user.getSkillLevel(SkillAPI.getSkill("BlastResistance"));
				if(level < 5)
					event.setDamage(event.getDamage() * (1.0-0.2*level));
				else
					event.setCancelled(true);
			}
			if(event.getCause() == DamageCause.FALL && user.hasSkill(SkillAPI.getSkill("Weightless"), 1))
			{
				int level = user.getSkillLevel(SkillAPI.getSkill("Weightless"));
				if(level < 5)
					event.setDamage(event.getDamage() * (1.0-0.2*level));
				else
					event.setCancelled(true);
			}
			if(event.getCause() == DamageCause.POISON && user.hasSkill(SkillAPI.getSkill("PoisonResistance"), 1))
			{
				int level = user.getSkillLevel(SkillAPI.getSkill("PoisonResistance"));
				if(level < 5)
					event.setDamage(event.getDamage() * (1.0-0.2*level));
				else
					event.setCancelled(true);
			}
			if(event.getCause() == DamageCause.WITHER && user.hasSkill(SkillAPI.getSkill("WitherResistance"), 1))
			{
				int level = user.getSkillLevel(SkillAPI.getSkill("WitherResistance"));
				if(level < 5)
					event.setDamage(event.getDamage() * (1.0-0.2*level));
				else
					event.setCancelled(true);
			}
			if(event.getCause() == DamageCause.PROJECTILE && user.hasSkill(SkillAPI.getSkill("ProjectileResistance"), 1))
			{
				int level = user.getSkillLevel(SkillAPI.getSkill("ProjectileResistance"));
				if(level < 5)
					event.setDamage(event.getDamage() * (1.0-0.2*level));
				else
					event.setCancelled(true);
			}
			if(event.getCause() == DamageCause.DROWNING && user.hasSkill(SkillAPI.getSkill("Gills"), 1))
			{
				event.setCancelled(true);
			}
			if((event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.HOT_FLOOR) && user.hasSkill(SkillAPI.getSkill("FireResistance"), 1))
			{
				int level = user.getSkillLevel(SkillAPI.getSkill("FireResistance"));
				if(level < 5)
					event.setDamage(event.getDamage() * (1.0-0.2*level));
				else
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void entityDamageByEntity(EntityDamageByEntityEvent event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof Player)
		{
			Player player = (Player) ent;
			User user = em.getMain().getUserManager().getUser(player);
			if(event.getDamager() instanceof Player && user.hasSkill(SkillAPI.getSkill("Thorns"), 1))
			{
				int level = user.getSkillLevel(SkillAPI.getSkill("Thorns"));
				((Player) event.getDamager()).damage(event.getDamage()*0.2*level);
			}
		}
	}

}
