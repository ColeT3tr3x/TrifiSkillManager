package facejup.skillpack.skills.targettedskills;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.TargetSkill;

import facejup.skillpack.main.Main;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;
import net.md_5.bungee.api.ChatColor;

public class Leviosa extends Skill implements TargetSkill{

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();

	private final double COOLDOWN = 10;
	private final double MANACOST = 20;
	private final double RANGE = 5;
	private final double RANGE_SCALE = 2;

	public Leviosa(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
	}

	@Override
	public double getManaCost(int level) {
		return MANACOST;
	}

	@Override
	public double getRange(int level) {
		return RANGE + RANGE_SCALE*level;
	}
	
	@Override
	public double getCooldown(int level)
	{
		return COOLDOWN;
	}

	@Override
	public boolean cast(LivingEntity caster, LivingEntity target, int level, boolean arg3) {
		if(!(caster instanceof Player && target instanceof LivingEntity))
			return false;
		if(this.isOnCooldown(caster, level))
		{
			DecimalFormat format = new DecimalFormat("##.##");
			caster.sendMessage("Can't cast for " + String.format("" + ((cooldown.get(caster)+this.getCooldown(level)*1000-System.currentTimeMillis())/1000.0), format) + " seconds.");
			return false;
		}
		User user = Main.getPlugin(Main.class).getUserManager().getUser((OfflinePlayer)caster);
		if(!user.decMana((int) MANACOST))
		{
			caster.sendMessage(Lang.tag + Chat.translate("&5Not enough mana! &c(" + user.getMana() + "/" + MANACOST + ")"));
			return false;
		}
		cooldown.put(caster, System.currentTimeMillis());
		caster.sendMessage(Chat.translate(Lang.tag + "You cast Leviosa on " + target.getName()));
		target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20+level*10, level));
		return true;
	}

	public boolean isOnCooldown(LivingEntity shooter, int level)
	{
		if(cooldown.containsKey(shooter))
			return (cooldown.get(shooter)+this.getCooldown(level)*1000 > System.currentTimeMillis());
		return false;
	}

}
