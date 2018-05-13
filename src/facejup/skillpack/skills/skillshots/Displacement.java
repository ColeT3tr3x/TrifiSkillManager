package facejup.skillpack.skills.skillshots;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillAttribute;
import com.sucy.skill.api.skills.SkillShot;

import facejup.skillpack.main.Main;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;

public class Displacement extends Skill implements SkillShot {

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();
	
	private final double COOLDOWN = 5;
	private final double MANACOST = 25;
	private final int COST = 7;
	private final int COST_SCALE = 2;
	
	public Displacement(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Teleport you %LEVEL*3% blocks forward");
		settings.set(SkillAttribute.MANA, MANACOST);
	}

	@Override
	public boolean cast(LivingEntity player, int level) {
		if(!(player instanceof Player))
			return false;
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
		Location loc = player.getLocation().clone();
		for(int i = 0; i < level*3; i++)
		{
			loc.add(player.getLocation().getDirection());
		}
		String distance = format.format(loc.distance(player.getLocation()));
		player.setFallDistance(0);
		player.teleport(loc);
		player.sendMessage(Lang.tag + Chat.translate("You teleported " + distance + " blocks."));
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
