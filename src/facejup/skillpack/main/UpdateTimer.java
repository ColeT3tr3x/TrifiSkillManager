package facejup.skillpack.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.users.User;

public class UpdateTimer {

	private Main main;
	private boolean running;
	
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
				if(user.getPlayer().isOnline())
					user.incMana(10);
				for(Skill skill : user.getSkills())
				{
					if(skill instanceof PassiveSkill)
						((PassiveSkill) skill).update(player, user.getSkillLevel(skill), 0);
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
	
}
