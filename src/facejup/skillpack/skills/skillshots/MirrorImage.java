package facejup.skillpack.skills.skillshots;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;

import facejup.skillpack.main.Main;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;

public class MirrorImage extends Skill implements SkillShot{

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();
	public HashMap<Player, List<Zombie>> clones = new HashMap<>();

	private final double COOLDOWN = 0.1;
	private final double MANACOST = 20;
	private final int COST = 45;
	private final int COST_SCALE = 5;

	public MirrorImage(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Create %LEVEL% clones of yourself.");
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
			addClone((Player)shooter);
		}
		Main main = Main.getPlugin(Main.class);
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
		{
			public void run()
			{
				for(Zombie zombie : clones.get(shooter))
				{
					zombie.remove();
				}
				clones.remove(shooter);
			}
		}, 100+level*30L);
		return true;
	}

	public void addClone(Player player)
	{
		List<Zombie> cloneList = new ArrayList<>();
		if(clones.containsKey(player))
			cloneList = clones.get(player);
		Zombie zombie = ((Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE));
		zombie.leaveVehicle();
		if(player.getInventory().getItemInMainHand() != null)
			zombie.getEquipment().setItemInMainHand(player.getInventory().getItemInMainHand());
		if(player.getInventory().getHelmet() != null)
			zombie.getEquipment().setHelmet(player.getInventory().getHelmet());
		if(player.getInventory().getChestplate() != null)
			zombie.getEquipment().setChestplate(player.getInventory().getChestplate());
		if(player.getInventory().getLeggings() != null)
			zombie.getEquipment().setLeggings(player.getInventory().getLeggings());
		if(player.getInventory().getBoots() != null)
			zombie.getEquipment().setBoots(player.getInventory().getBoots());
		PlayerDisguise disguise = new PlayerDisguise(player.getName());
		disguise.setEntity(zombie);
		disguise.setViewSelfDisguise(false);
		disguise.setVelocitySent(true);
		disguise.startDisguise();
		cloneList.add(zombie);
		clones.put(player, cloneList);
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
