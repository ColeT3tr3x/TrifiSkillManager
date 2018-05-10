package facejup.skillpack.main;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;

public class UpdateTimer {

	private Main main;
	private boolean running;

	private HashMap<Player, Double> sprinting = new HashMap<>();

	public UpdateTimer(Main main)
	{
		this.main = main;
	}

	public void startTimer()
	{
		running = true;
		runTimer();
	}

	private void runTimer()
	{
		if(running)
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{
				User user = main.getUserManager().getUser(player);
				if(!sprinting.containsKey(player))
				{
					sprinting.put(player, 20d);
				}
				if(user.getPlayer().isOnline() && (!player.isSprinting() || sprinting.get(player) > 0))
					user.incMana(10);
				player.setFoodLevel((int) (user.getMana()/100.0*20));
				for(Skill skill : user.getSkills())
				{
					if(skill instanceof PassiveSkill)
						((PassiveSkill) skill).update(player, user.getSkillLevel(skill), 0);
				}
				if(player.isSprinting())
				{
					if(sprinting.get(player) - 1.5 > 0)
					{
						sprinting.put(player, sprinting.get(player)-1.5);
					}
					else
					{
						sprinting.put(player, 0d);
						user.decMana(18);
					}
					Chat.sendActionBar(player, getSprintString(player));
				}
				else
				{
					if(!sprinting.containsKey(player))
						sprinting.put(player, 20d);
					else
					{
						if(user.getMana() > 50)
						{
							if(sprinting.get(player) + 2.5 < 20d)
							{
								Chat.sendActionBar(player, getSprintString(player));
								sprinting.put(player, sprinting.get(player)+2.5);
							}
							else if(sprinting.get(player) != 20d)
							{
								Chat.sendActionBar(player, getSprintString(player));
								sprinting.put(player, 20d);
							}
						}
					}
				}
			}
			main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
			{
				public void run()
				{
					runTimer();
				}
			}, 20L);
		}
	}
	
	public String getSprintString(Player player)
	{
		String str = "";
		Double sprint = sprinting.get(player);
		if(sprint > 0)
			str+= Chat.translate("&a-");
		else
			str+= Chat.translate("&7-");
		if(sprint > 2)
			str+= Chat.translate("&a-");
		else
			str+= Chat.translate("&7-");
		if(sprint > 4)
			str+= Chat.translate("&aS");
		else
			str+= Chat.translate("&7S");
		if(sprint > 6)
			str+= Chat.translate("&aP");
		else
			str+= Chat.translate("&7P");
		if(sprint > 8)
			str+= Chat.translate("&aR");
		else
			str+= Chat.translate("&7R");
		if(sprint > 10)
			str+= Chat.translate("&aI");
		else
			str+= Chat.translate("&7I");
		if(sprint > 12)
			str+= Chat.translate("&aN");
		else
			str+= Chat.translate("&7N");
		if(sprint > 14)
			str+= Chat.translate("&aT");
		else
			str+= Chat.translate("&7T");
		if(sprint > 16)
			str+= Chat.translate("&a-");
		else
			str+= Chat.translate("&7-");
		if(sprint > 18)
			str+= Chat.translate("&a-");
		else
			str+= Chat.translate("&7-");
		return str;
	}

}
