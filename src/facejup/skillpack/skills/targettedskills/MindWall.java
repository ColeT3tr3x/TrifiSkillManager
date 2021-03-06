package facejup.skillpack.skills.targettedskills;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.TargetSkill;

import facejup.skillpack.main.Main;
import facejup.skillpack.skills.IPlayerTarget;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;

public class MindWall extends Skill implements TargetSkill,IPlayerTarget {

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();
	private final double COOLDOWN = 5;
	private final double MANACOST = 20;
	private final double RANGE = 20;


	public MindWall(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);getDescription().add("Make the target player believe they are trapped.");
	}

	@Override
	public boolean cast(LivingEntity caster, LivingEntity target, int level, boolean arg3) {
		if(!(caster instanceof Player && target instanceof Player))
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
		Player player = (Player) caster;
		Player player2 = (Player) target;
		List<Location> locs = new ArrayList<>();
		double radius = player.getLocation().distance(player2.getLocation())/2;
		for(int x = -10; x <= 10; x++)
		{
			for(int y = -1; y <= 3; y++)
			{
				for(int z = -10; z <= 10; z++)
				{
					Location loc = player.getLocation().add(new Vector(x,y,z));
					Vector dir = loc.toVector().subtract(player.getEyeLocation().toVector()).normalize();
					double dot = dir.dot(player.getEyeLocation().getDirection());
					if(dot >= 0.75 && loc.distance(player.getEyeLocation()) < (radius+0.5) && loc.distance(player.getEyeLocation()) > (radius-0.5))
					{
						locs.add(loc);
						player2.sendBlockChange(loc, Material.COBBLE_WALL, (byte) 0);
					}
				}
			}
		}
		player.sendMessage(Chat.translate(Lang.tag + "You cast MindWall on " + player2.getName()));
		Main main = Main.getPlugin(Main.class);
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
		{
			public void run()
			{
				for(Location loc : locs)
				{
					player2.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
				}
			}
		}, 40L+level*20L);
		return true;
	}

	public boolean isOnCooldown(LivingEntity shooter, int level)
	{
		if(cooldown.containsKey(shooter))
			return (cooldown.get(shooter)+this.getCooldown(level)*1000 > System.currentTimeMillis());
		return false;
	}

	public double getRange(int level)
	{
		return RANGE;
	}


}
