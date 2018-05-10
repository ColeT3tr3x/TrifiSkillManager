package facejup.skillpack.skills.skillshots;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Arrow.PickupStatus;
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
import net.md_5.bungee.api.chat.TextComponent;

public class Eruption extends Skill implements SkillShot{

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();

	private final double COOLDOWN = 0.1;
	private final double MANACOST = 20;

	public Eruption(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Shoot %LEVEL*3% +2 explosions in front of you");
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
		shoot((Player) shooter, level, 1);
		return true;
	}

	public void shoot(Player shooter, int level, double iter)
	{
		if(iter <= level*3+2)
		{
			Location loc3 = shooter.getLocation();
			loc3.add(loc3.getDirection().multiply(3)).add(loc3.getDirection().multiply(iter*3));
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
			loc3.getWorld().createExplosion(loc3, 2, true);
			Main main = Main.getPlugin(Main.class);
			main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
			{
				public void run()
				{
					shoot(shooter, level, iter+1);
				}
			}, 1L);
		}
	}

	public boolean isOnCooldown(LivingEntity shooter, int level)
	{
		if(cooldown.containsKey(shooter))
			return (cooldown.get(shooter)+this.getCooldown(level)*1000 > System.currentTimeMillis());
		return false;
	}

}
