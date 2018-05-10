package facejup.skillpack.skills.skillshots;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;

import facejup.skillpack.main.Main;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;

public class Gravitize extends Skill implements SkillShot{

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();

	private final double COOLDOWN = 0.1;
	private final double MANACOST = 20;

	public Gravitize(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Cause blocks around you to fall");
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
		int limit = level*2+3;
		for(int y = limit*-1; y < limit; y++)
		{
			for(int x = limit*-1; x < limit; x++)
			{
				for(int z = limit*-1; z < limit; z++)
				{
					Location loc = shooter.getLocation().clone().add(new Vector(x,y,z));
					if(loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
					{
						MaterialData mat = new MaterialData(loc.getBlock().getType(), loc.getBlock().getData());
						loc.getBlock().setType(Material.AIR);
						loc.getWorld().spawnFallingBlock(loc, mat);
					}
				}
			}
		}
		return true;
	}

	public boolean isOnCooldown(LivingEntity shooter, int level)
	{
		if(cooldown.containsKey(shooter))
			return (cooldown.get(shooter)+this.getCooldown(level)*1000 > System.currentTimeMillis());
		return false;
	}

}
