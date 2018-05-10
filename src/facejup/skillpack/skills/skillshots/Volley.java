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

public class Volley extends Skill implements SkillShot{

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();

	private final double COOLDOWN = 0.1;
	private final double MANACOST = 20;

	public Volley(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Shoot %LEVEL*3% +2 arrows in front of you");
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
			if(((level*3+2)/(iter+2)) * 1L == 1)
			{
				Arrow arrow = shooter.launchProjectile(Arrow.class);
				arrow.setVelocity(arrow.getVelocity().multiply(0.2 + (0.03*level)));
				arrow.setPickupStatus(PickupStatus.DISALLOWED);
				iter+=1;
				iter+=1;
			}
			Arrow arrow = shooter.launchProjectile(Arrow.class);
			arrow.setVelocity(arrow.getVelocity().multiply(0.2 + (0.03*level)));
			arrow.setPickupStatus(PickupStatus.DISALLOWED);
			Main main = Main.getPlugin(Main.class);
			double newiter = iter;
			main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
			{
				public void run()
				{
					shoot(shooter, level, newiter+1);
				}
			}, (long) (((level*3+2)/(iter+2)) * 1L));
		}
	}

	public boolean isOnCooldown(LivingEntity shooter, int level)
	{
		if(cooldown.containsKey(shooter))
			return (cooldown.get(shooter)+this.getCooldown(level)*1000 > System.currentTimeMillis());
		return false;
	}

}
