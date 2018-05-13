package facejup.skillpack.users;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.skills.skillshots.IUnlocker;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;

public class User {

	private UserManager um;

	private ConfigurationSection section;

	private OfflinePlayer player;

	private int mana;

	public User(UserManager um, OfflinePlayer player)
	{
		//Constructor which loads and saves the information from the file.
		this.player = player;
		this.um = um;
		if(um.getFileControl() != null && um.getFileControl().getFile().exists()) // Checks to make sure the file exists.
		{
			FileConfiguration config = um.getFileControl().getConfig(); // Get the Users.yml config
			if(config.contains("Users." + player.getUniqueId())) // Checks if the player is in the config.
			{
				// Store their section if they are.
				this.section = config.getConfigurationSection("Users." + player.getUniqueId());
				um.getFileControl().save();
			}
			else
			{
				// Otherwise, set their default information and save. Then store their section.
				config.set("Users." + player.getUniqueId() + ".Name", player.getName());
				config.set("Users." + player.getUniqueId() + ".Skillpoints", 0);
				config.set("Users." + player.getUniqueId() + ".Level", 1);
				config.set("Users." + player.getUniqueId() + ".Experience", 0);
				/*config.set("Users." + player.getUniqueId() + ".Skills.Fireball.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.FireballRare.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.Ignite.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.Displacement.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.Drill.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.Volley.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.Magnetize.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.Gravitize.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.Static.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.Eruption.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.Meteor.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.Gift.Level", 1);
				config.set("Users." + player.getUniqueId() + ".Skills.FireLeap.Level", 1);*/
				um.getFileControl().save(config);
				this.section = config.getConfigurationSection("Users." + player.getUniqueId());
			}
		}
	}

	public void purchaseSkill(Skill skill)
	{
		if(skill.getCost(1) == 0)
			return;
		if(hasSkill(skill, 1))
		{
			if(player.isOnline())
				((Player) player).sendMessage(Chat.translate(Lang.tag + "You already have that skill."));
			return;
		}
		if(getSkillpoints() >= skill.getCost(1))
		{
			decSkillpoints(skill.getCost(1));
			unlockSkill(skill);
		}
		else
		{
			if(player.isOnline())
				((Player) player).sendMessage(Chat.translate(Lang.tag + "Not enough Skillpoints &5(" + getSkillpoints() + "/" + skill.getCost(1) + ")"));
		}
	}

	public void purchaseSkill(Skill skill, int level)
	{
		if(skill.getCost(level) == 0)
			return;
		if(hasSkill(skill, level))
		{
			if(player.isOnline())
				((Player) player).sendMessage(Chat.translate(Lang.tag + "You already have that skill."));
			return;
		}
		if(getSkillpoints() >= skill.getCost(level))
		{
			decSkillpoints(skill.getCost(level));
			unlockSkill(skill, level);
		}
		else
		{
			if(player.isOnline())
				((Player) player).sendMessage(Chat.translate(Lang.tag + "Not enough Skillpoints &5(" + getSkillpoints() + "/" + skill.getCost(level) + ")"));
		}
	}

	public boolean hasSkill(Skill skill, int level)
	{
		if(section.contains("Skills." + skill.getName()) && section.getInt("Skills." + skill.getName() + ".Level") >= level)
			return true;
		return false;
	}

	public void unlockSkill(Skill skill)
	{
		if(player.isOnline())
			((Player) player).sendMessage(Chat.translate(Lang.tag + "You have unlocked " + skill.getName()));
		section.set("Skills." + skill.getName() + ".Level", 1);
		um.getFileControl().save();
	}

	public void unlockSkill(Skill skill, int level)
	{
		if(player.isOnline())
			((Player) player).sendMessage(Chat.translate(Lang.tag + "You have unlocked " + skill.getName()));
		section.set("Skills." + skill.getName() + ".Level", level);
		um.getFileControl().save();
	}

	public int getSkillLevel(Skill skill)
	{
		if(section.contains("Skills." + skill.getName() + ".Level"))
			return section.getInt("Skills." + skill.getName() + ".Level");
		return 0;
	}

	public List<Skill> getSkills()
	{
		List<Skill> skills = new ArrayList<>();
		if(section.contains("Skills"))
		{
			for(String key : section.getConfigurationSection("Skills").getKeys(false))
			{
				if(SkillAPI.getSkill(key) != null)
					skills.add(SkillAPI.getSkill(key));
			}
		}
		return skills;
	}

	public void incSkillLevel(Skill skill)
	{
		if(section.contains("Skills." + skill.getName() + ".Level"))
		{
			int level = section.getInt("Skills." + skill.getName() + ".Level");
			if(level+1 <= skill.getMaxLevel())
				section.set("Skills." + skill.getName() + ".Level", section.getInt("Skills." + skill.getName() + ".Level")+1);
			if(level+1 == skill.getMaxLevel())
			{
				if(skill instanceof IUnlocker)
				{
					if(SkillAPI.getSkill(((IUnlocker)skill).getSkillName()) != null)
					{
						unlockSkill(SkillAPI.getSkill(((IUnlocker)skill).getSkillName()));
					}
				}
			}
			um.getFileControl().save();
		}
	}

	public void attemptIncSkillLevel(Skill skill)
	{
		if(getSkillpoints() > 0 && getSkillLevel(skill) < skill.getMaxLevel())
		{
			decSkillpoints();
			incSkillLevel(skill);
		}
	}

	public boolean decMana(int i)
	{
		if(mana >= i)
		{
			mana -= i;
			if(player.isOnline())
			{
				((Player) player).setFoodLevel((int) (mana/100.0*20));
			}
			return true;
		}
		else
			return false;
	}

	public void incExp(int i)
	{
		if(section.contains("Experience"))
		{
			int xp = section.getInt("Experience")+i;
			section.set("Experience", xp);
			if(getLevelProgress() >= getLevelExp(getLevel()))
			{
				incLevel();
			}
		}
		um.getFileControl().save();
	}

	public int getExp()
	{
		if(!section.contains("Experience"))
			section.set("Experience", 0);
		um.getFileControl().save();
		return section.getInt("Experience");
	}

	public void incLevel()
	{
		if(!section.contains("Level"))
			section.set("Level", 0);
		int level = section.getInt("Level")+1;
		section.set("Level", level);
		incSkillPoints();
		um.getFileControl().save();
		if(player.isOnline())
		{
			((Player) player).sendMessage(Lang.tag + Chat.translate("&bLevel up! &dYou have reached level " + level + "!"));
			playSound((Player) player);
		}
		if(getLevelProgress() >= getLevelExp(getLevel()))
		{
			incLevel();
		}
	}

	public void playSound(Player player)
	{
		player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1, 1);
	}

	public void incSkillPoints()
	{
		if(section.contains("Skillpoints"))
			section.set("Skillpoints", section.getInt("Skillpoints")+1);
		else
			section.set("Skillpoints", 1);
		um.getFileControl().save();
	}

	public int getLevelProgress()
	{
		int level = getLevel();
		int xp = getExp();
		if(level > 1)
			for(int i = 1; i < level; i++)
				xp -= getLevelExp(i);
		return xp;
	}

	public int getLevelExp(int level)
	{
		double calc = 0;
		if(level < 101)
			calc+= 20*Math.pow(1.1, level);
		else
		{
			calc+=20*Math.pow(1.1, 100);
			calc += Math.pow(1.2, level-100);
		}
		return (int) calc;
	}

	public int getLevel()
	{
		if(section.contains("Level"))
			return section.getInt("Level");
		return 1;
	}

	public void incMana(int i)
	{
		if(mana+i <= 100)
		{
			mana+=i;
			if(player.isOnline())
			{
				((Player) player).setFoodLevel((int) (mana/100.0*20));
			}
		}
	}

	public int getSkillpoints()
	{
		if(section.contains("Skillpoints"))
			return section.getInt("Skillpoints");
		return 0;
	}

	public boolean decSkillpoints()
	{
		if(section.contains("Skillpoints"))
		{
			if(section.getInt("Skillpoints") >= 1)
			{
				section.set("Skillpoints", section.getInt("Skillpoints")-1);
				return true;
			}
			else if(player.isOnline())
				((Player)player).sendMessage(Chat.translate(Lang.tag + "Not enough skillpoints"));
		}
		um.getFileControl().save();
		return false;
	}

	public boolean decSkillpoints(int i)
	{
		if(section.contains("Skillpoints"))
		{
			if(section.getInt("Skillpoints") >= i)
			{
				section.set("Skillpoints", section.getInt("Skillpoints")-i);
				return true;
			}
			else if(player.isOnline())
				((Player)player).sendMessage(Chat.translate(Lang.tag + "Not enough skillpoints"));
		}
		um.getFileControl().save();
		return false;
	}

	public OfflinePlayer getPlayer()
	{
		return this.player;
	}

	public int getMana()
	{
		return this.mana;
	}

}
