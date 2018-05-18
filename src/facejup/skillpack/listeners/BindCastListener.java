package facejup.skillpack.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.api.skills.TargetSkill;

import facejup.skillpack.main.EventManager;
import facejup.skillpack.skills.IPlayerTarget;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;
import facejup.skillpack.util.Numbers;
import facejup.skillpack.util.SkillUtil;
import net.md_5.bungee.api.ChatColor;

public class BindCastListener implements Listener{

	private EventManager em;

	public BindCastListener(EventManager em)
	{
		this.em = em;
		em.getMain().getServer().getPluginManager().registerEvents(this, em.getMain());
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if(event.getAction().name().contains("RIGHT_CLICK") && player.getGameMode() != GameMode.SPECTATOR)
		{
			if(event.getItem() == null)
				return;
			if(em.getMain().getBindedSkill(player, item) != null)
			{
				Skill skill = em.getMain().getBindedSkill(player, item);
				User user = em.getMain().getUserManager().getUser(player);
				if(skill instanceof SkillShot)
				{
					if(user.getSkillLevel(skill) > 0)
					{
						((SkillShot) skill).cast(player, em.getMain().getBindedSkillLevel(player, skill));
					}
				}
				else if(skill instanceof TargetSkill)
				{
					if(user.getSkillLevel(skill) > 0)
					{
						if(skill instanceof IPlayerTarget)
						{
							if(!SkillUtil.isTargettingPlayer(player, skill.getRange(em.getMain().getBindedSkillLevel(player, skill))))
							{
								player.sendMessage(Chat.translate(Lang.tag + "Must target a player"));
								return;
							}
							((TargetSkill)skill).cast(player, (LivingEntity) SkillUtil.getTarget(player, skill.getRange(em.getMain().getBindedSkillLevel(player, skill))), em.getMain().getBindedSkillLevel(player, skill), false);
						}
						else
						{
							if(!SkillUtil.isTargettingEntity(player, skill.getRange(em.getMain().getBindedSkillLevel(player, skill))))
							{
								player.sendMessage(Chat.translate(Lang.tag + "Must target an entity"));
								return;
							}			
							((TargetSkill)skill).cast(player, (LivingEntity) SkillUtil.getTarget(player, skill.getRange(em.getMain().getBindedSkillLevel(player, skill))), em.getMain().getBindedSkillLevel(player, skill), false);
						}
					}
				}
			}
			if(item.getItemMeta().hasLore())
			{
				for(String loreline : item.getItemMeta().getLore())
				{
					loreline = ChatColor.stripColor(loreline);
					if(!loreline.contains(" "))
						continue;
					if(SkillAPI.getSkill(loreline.substring(0, loreline.indexOf(" "))) != null)
					{
						Skill skill = SkillAPI.getSkill(loreline.substring(0, loreline.indexOf(" ")));
						if(skill instanceof SkillShot)
						{
							int level = 1;
							if(Numbers.isInt(loreline.substring(loreline.indexOf(" ")+1)))
								level = Integer.parseInt(loreline.substring(loreline.indexOf(" ")+1));
							User user = em.getMain().getUserManager().getUser(player);
							user.incMana(20);
							((SkillShot)skill).cast(player, level);
						}
						else if(skill instanceof TargetSkill)
						{
							if(skill instanceof IPlayerTarget)
							{
								int level = 1;
								if(Numbers.isInt(loreline.substring(loreline.indexOf(" ")+1)))
									level = Integer.parseInt(loreline.substring(loreline.indexOf(" ")+1));
								if(!SkillUtil.isTargettingPlayer(player, skill.getRange(level)))
								{
									player.sendMessage(Chat.translate(Lang.tag + "Must target a player"));
									return;
								}
								User user = em.getMain().getUserManager().getUser(player);
								user.incMana(20);
								((TargetSkill)skill).cast(player, (LivingEntity) SkillUtil.getTarget(player, skill.getRange(level)), level, false);
							}
							else
							{
								int level = 1;
								if(Numbers.isInt(loreline.substring(loreline.indexOf(" ")+1)))
									level = Integer.parseInt(loreline.substring(loreline.indexOf(" ")+1));
								if(!SkillUtil.isTargettingEntity(player, skill.getRange(level)))
								{
									player.sendMessage(Chat.translate(Lang.tag + "Must target an entity"));
									return;
								}
								User user = em.getMain().getUserManager().getUser(player);
								user.incMana(20);
								((TargetSkill)skill).cast(player, (LivingEntity) SkillUtil.getTarget(player, skill.getRange(level)), level, false);
							}
						}
					}
				}
			}
		}
	}
}
