package facejup.skillpack.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.main.EventManager;
import facejup.skillpack.main.Main;
import facejup.skillpack.skills.skillshots.LightningBall;
import facejup.skillpack.skills.skillshots.Projection;
import facejup.skillpack.skills.skillshots.Pyromancy;
import facejup.skillpack.skills.skillshots.Telekinesis;
import facejup.skillpack.users.User;
import facejup.skillpack.util.SkillUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.npc.NPC;

public class SkillListener implements Listener {

	private EventManager em;

	public SkillListener(EventManager em)
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
			if(user.hasSkill(SkillAPI.getSkill("Projection"), 1) & user.getSkillLevel(SkillAPI.getSkill("Projection")) < 2)
			{
				Projection projskill = (Projection) SkillAPI.getSkill("Projection");
				if(projskill.clones.containsKey(player))
				{
					if(projskill.clones.get(player).getStoredLocation().distance(player.getLocation()) < 2)
					{
						NPC npc = projskill.clones.get(player);
						if(projskill.castlevel.get(player) < 3)
							player.teleport(npc.getStoredLocation());
						npc.despawn();
						CitizensAPI.getNPCRegistry().deregister(npc);
						projskill.clones.remove(player);
						player.setGameMode(GameMode.SURVIVAL);
					}
				}
			}
		}
	}

	@EventHandler
	public void npcDamageByEntity(NPCDamageByEntityEvent event)
	{
		Projection projskill = (Projection) SkillAPI.getSkill("Projection");
		if(projskill.clones.values().contains(event.getNPC()))
		{
			for(Player player : projskill.clones.keySet())
			{
				if(projskill.clones.get(player).equals(event.getNPC()))
				{
					if(projskill.castlevel.get(player) < 2)
					{
						NPC npc = projskill.clones.get(player);
						if(projskill.castlevel.get(player) < 3)
							player.teleport(npc.getStoredLocation());
						npc.despawn();
						CitizensAPI.getNPCRegistry().deregister(npc);
						projskill.clones.remove(player);
						player.setGameMode(GameMode.SURVIVAL);
						player.setHealth(((LivingEntity)npc.getEntity()).getHealth());
					}
					else
					{
						NPC npc = projskill.clones.get(player);
						if(((LivingEntity)npc.getEntity()).getHealth()-event.getDamage() < 0)
						{
							player.setGameMode(GameMode.SURVIVAL);
							npc.despawn();
							CitizensAPI.getNPCRegistry().deregister(npc);
							projskill.clones.remove(player);
							player.damage(player.getMaxHealth());
						}
					}
				}
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
			if(SkillUtil.itemHasSkill(player.getInventory().getItemInMainHand(), SkillAPI.getSkill("Thorns"), 1) || user.hasSkill(SkillAPI.getSkill("Thorns"), 1))
			{
				Skill skill = SkillAPI.getSkill("Thorns");
				int itemlevel = SkillUtil.getItemSkillLevel(player.getInventory().getItemInMainHand(), skill);
				int playerlevel = user.getSkillLevel(skill);
				int level = itemlevel>playerlevel?itemlevel:playerlevel;
				((Player) event.getDamager()).damage(event.getDamage()*0.2*level);
			}
		}
	}

	@EventHandler
	public void blockFormEvent(EntityChangeBlockEvent event)
	{
		Pyromancy pyroskill = (Pyromancy) SkillAPI.getSkill("Pyromancy");
		if(pyroskill.fires.values().contains(event.getEntity()))
		{
			for(Player player : pyroskill.fires.keySet())
			{
				if(pyroskill.fires.get(player).equals(event.getEntity()))
				{
					pyroskill.fires.remove(player);
					return;
				}
			}
		}
		Telekinesis teleskill = (Telekinesis) SkillAPI.getSkill("Telekinesis");
		if(teleskill.blocks.values().contains(event.getEntity()))
		{
			for(Player player : teleskill.blocks.keySet())
			{
				if(teleskill.blocks.get(player).equals(event.getEntity()))
				{
					teleskill.blocks.remove(player);
					return;
				}
			}
		}
	}

	@EventHandler
	public void playerShooter(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Pyromancy pyroskill = (Pyromancy) SkillAPI.getSkill("Pyromancy");
		if(pyroskill.fires.containsKey(player))
		{
			if(!pyroskill.cooldown.containsKey(player))
				return;
			if(pyroskill.cooldown.get(player) + 1000 < System.currentTimeMillis())
			{
				User user = Main.getPlugin(Main.class).getUserManager().getUser(player);
				if(event.getAction()== Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
				{
					FallingBlock block = pyroskill.fires.get(player);
					pyroskill.fires.remove(player);
					block.setVelocity(player.getLocation().getDirection().multiply(0.5*user.getSkillLevel(pyroskill)));
					return;
				}
				if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					pyroskill.fires.remove(player);
				}
				pyroskill.cooldown.put(player, System.currentTimeMillis());
			}
		}
		Telekinesis teleskill = (Telekinesis) SkillAPI.getSkill("Telekinesis");
		if(teleskill.blocks.containsKey(player))
		{
			if(!teleskill.cooldown.containsKey(player))
				return;
			if(teleskill.cooldown.get(player) + 1000 < System.currentTimeMillis())
			{
				User user = Main.getPlugin(Main.class).getUserManager().getUser(player);
				if(event.getAction()== Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
				{
					FallingBlock block = teleskill.blocks.get(player);
					block.setVelocity(player.getLocation().getDirection().multiply(0.5*teleskill.castlevel.get(player)));
					teleskill.blocks.remove(player);
					return;
				}
				if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					teleskill.blocks.remove(player);
				}
				teleskill.cooldown.put(player, System.currentTimeMillis());
			}
		}
	}

	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		User user = em.getMain().getUserManager().getUser(player);
		boolean flag = false;
		for(Player p : player.getWorld().getPlayers())
		{
			boolean gorgonflag = em.getMain().getUserManager().getUser(p).hasSkill(SkillAPI.getSkill("Gorgon"), 1);
			boolean twistedflag = em.getMain().getUserManager().getUser(p).hasSkill(SkillAPI.getSkill("TwistedSight"), 1);
			if(!(gorgonflag || twistedflag))
				continue;
			if(p.getLocation().distance(player.getLocation()) < 20)
			{
				Vector dir = p.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
				double dot = dir.dot(player.getLocation().getDirection());
				if(dot > 0.98)
				{
					if(twistedflag)
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, em.getMain().getUserManager().getUser(p).getSkillLevel(SkillAPI.getSkill("TwistedSight"))));
					if(gorgonflag)
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, em.getMain().getUserManager().getUser(p).getSkillLevel(SkillAPI.getSkill("Gorgon"))));
					flag = true;
				}
			}
		}
		Projection projskill = (Projection) SkillAPI.getSkill("Projection");
		if(projskill.clones.containsKey(player))
		{
			if(projskill.clones.get(player).getStoredLocation().distance(player.getLocation()) > 10)
			{
				NPC npc = projskill.clones.get(player);
				if(projskill.castlevel.get(player) < 3)
					player.teleport(npc.getStoredLocation());
				npc.despawn();
				CitizensAPI.getNPCRegistry().deregister(npc);
				projskill.clones.remove(player);
				player.setGameMode(GameMode.SURVIVAL);
				return;
			}
			if(projskill.clones.get(player).getStoredLocation().distance(player.getLocation()) < 2)
			{
				NPC npc = projskill.clones.get(player);
				player.teleport(npc.getStoredLocation());
				npc.despawn();
				CitizensAPI.getNPCRegistry().deregister(npc);
				projskill.clones.remove(player);
				player.setGameMode(GameMode.SURVIVAL);
			}
		}
	}
	
	@EventHandler
	public void projectileHit(ProjectileHitEvent event)
	{
		LightningBall lightningballskill = (LightningBall) SkillAPI.getSkill("LightningBall");
		if(lightningballskill.balls.contains(event.getEntity()))
		{
			event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
			lightningballskill.balls.remove(event.getEntity());
		}
	}

}
