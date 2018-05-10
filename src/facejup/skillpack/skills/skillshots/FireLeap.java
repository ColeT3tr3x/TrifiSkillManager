package facejup.skillpack.skills.skillshots;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;

import facejup.skillpack.main.Main;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;

public class FireLeap extends Skill implements SkillShot{

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();

	private final double COOLDOWN = 5;
	private final double MANACOST = 25;
	private final int COST = 7;
	private final int COST_SCALE = 2;

	public FireLeap(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add(Chat.translate("&7Create a ring of fire and leap away from it"));
	}

	@Override
	public boolean cast(LivingEntity player, int level) {
		if(!(player instanceof Player))
			return false;
		if(!player.isOnGround())
		{
			player.sendMessage(Chat.translate(Lang.tag + "You must be on the ground"));
			return false;
		}
		DecimalFormat format = new DecimalFormat("##.00");
		if(this.isOnCooldown(player, level))
		{
			player.sendMessage("Can't cast for " + format.format(((cooldown.get(player)+this.getCooldown(level)*1000-System.currentTimeMillis())/1000.0)) + " seconds.");
			return false;
		}
		User user = Main.getPlugin(Main.class).getUserManager().getUser((OfflinePlayer)player);
		if(!user.decMana((int) MANACOST))
		{
			player.sendMessage(Lang.tag + Chat.translate("&5Not enough mana! &c(" + user.getMana() + "/" + MANACOST + ")"));
			return false;
		}
		cooldown.put(player, System.currentTimeMillis());
		callFireFall(player.getLocation(),level, 1);
		player.setVelocity(player.getLocation().getDirection().multiply(2));

		return true;
	}

	public void callFireFall(Location loc, int level, int radius)
	{
		if(radius < level+2)
		{
			for(int x = -1*radius; x <= radius; x++)
			{
				for(int z = -1*radius; z <= radius; z++)
				{
					Location tempLoc = loc.clone().add(new Vector(x,0,z));
					if(tempLoc.distance(loc) < (radius+0.5) && tempLoc.distance(loc) > (radius-0.5))
					{
						if(tempLoc.getBlock().getType() == Material.AIR)
						{
							tempLoc.getWorld().spawnFallingBlock(tempLoc, Material.FIRE, (byte) 0);
						}
					}
				}
			}
			Main main = Main.getPlugin(Main.class);
			main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
			{
				public void run()
				{
					callFireFall(loc, level, radius+1);
				}
			}, 2L);
		}
	}
	public boolean isOnCooldown(LivingEntity shooter, int level)
	{
		if(cooldown.containsKey(shooter))
			return (cooldown.get(shooter)+this.getCooldown(level)*1000 > System.currentTimeMillis());
		return false;

	}

	@Override
	public int getCost(int level)
	{
		return COST + level*COST_SCALE;
	}

}
