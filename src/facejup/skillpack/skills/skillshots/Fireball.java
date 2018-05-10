package facejup.skillpack.skills.skillshots;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;

import facejup.skillpack.main.Main;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;

public class Fireball extends Skill implements SkillShot{

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();

	private final double COOLDOWN = 0.1;
	private final double MANACOST = 20;
	private final int COST = 3;
	private final int COST_SCALE = 2;

	public Fireball(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Shoot &d(%LEVEL%) &7fireballs at the target");
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
		for(int i = 0; i < level; i++)
		{
			SmallFireball ball = shooter.launchProjectile(SmallFireball.class);
			ball.setVelocity(shooter.getLocation().getDirection().multiply(+0.03*level));
		}
		shooter.sendMessage("You cast a fireball");
		return true;
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
