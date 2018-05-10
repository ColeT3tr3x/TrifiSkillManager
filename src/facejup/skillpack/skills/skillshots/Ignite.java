package facejup.skillpack.skills.skillshots;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;

import facejup.skillpack.main.Main;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;

public class Ignite extends Skill implements SkillShot{

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();

	private final double COOLDOWN = 0.1;
	private final double MANACOST = 20;
	private final int COST = 4;
	private final int COST_SCALE = 1;

	public Ignite(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Launch fire infront of you");
		settings.set("Damage", 3, 1);
	}

	@Override
	public boolean cast(LivingEntity shooter, int level) {
		if(!(shooter instanceof Player))
			return false;
		if(this.isOnCooldown(shooter, level))
		{
			DecimalFormat format = new DecimalFormat("##.##");
			shooter.sendMessage("Can't cast for " + String.format("" + ((cooldown.get(shooter)+this.getCooldown(level)*1000-System.currentTimeMillis())/1000.0), format) + " seconds.");
			return false;
		}
		User user = Main.getPlugin(Main.class).getUserManager().getUser((OfflinePlayer)shooter);
		if(!user.decMana((int) MANACOST))
		{
			shooter.sendMessage(Lang.tag + Chat.translate("&5Not enough mana! &c(" + user.getMana() + "/" + MANACOST + ")"));
			return false;
		}
		cooldown.put(shooter, System.currentTimeMillis());
		fireCone((Player) shooter,shooter.getEyeLocation().subtract(new Vector(0,0.5,0)), level, 1);
		return true;
	}

	public void fireCone(Player shooter, Location loc, int level, double iter)
	{
		if(iter <= level*2+3)
		{
			Location loc3 = loc.clone();
			loc3.add(loc3.getDirection().multiply(iter));
			if(loc3.getWorld().getNearbyEntities(loc3, 0.5, 0.5, 0.5).size() >0)
			{
				for(Entity ent : loc3.getWorld().getNearbyEntities(loc3, 0.5, 0.5, 0.5))
				{
					if(ent instanceof Damageable)
					{
						((Damageable)ent).damage(this.settings.getInt("Damage"));
					}
				}
			}
			loc3.getWorld().spawnFallingBlock(loc3, Material.FIRE, (byte) 0);
			Main main = Main.getPlugin(Main.class);
			main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
			{
				public void run()
				{
					fireCone(shooter, loc, level, iter+1);
				}
			}, 4L);
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
