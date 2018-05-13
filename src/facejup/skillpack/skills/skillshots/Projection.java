package facejup.skillpack.skills.skillshots;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillAttribute;
import com.sucy.skill.api.skills.SkillShot;

import facejup.skillpack.main.Main;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class Projection extends Skill implements SkillShot {

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();
	public HashMap<Player, NPC> clones = new HashMap<>();
	public HashMap<Player, Integer> castlevel = new HashMap<>();

	private final double COOLDOWN = 5;
	private final double MANACOST = 25;
	private final int COST = 7;
	private final int COST_SCALE = 2;

	public Projection(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Travel outside of your body");
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
		Player caster = (Player) player;
		castlevel.put(caster, level);
		clones.put(caster, CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, caster.getName()));
		clones.get(caster).spawn(caster.getLocation());
		NPC npc = clones.get(caster);
		npc.setProtected(false);
		npc.getEntity().setInvulnerable(false);
		if(caster.getInventory().getHelmet() != null)
			((HumanEntity) npc.getEntity()).getEquipment().setHelmet(caster.getInventory().getHelmet());
		if(caster.getInventory().getChestplate() != null)
			((HumanEntity) npc.getEntity()).getEquipment().setChestplate(caster.getInventory().getChestplate());
		if(caster.getInventory().getLeggings() != null)
			((HumanEntity) npc.getEntity()).getEquipment().setLeggings(caster.getInventory().getLeggings());
		if(caster.getInventory().getBoots() != null)
			((HumanEntity) npc.getEntity()).getEquipment().setBoots(caster.getInventory().getBoots());
		if(caster.getInventory().getItemInMainHand() != null)
			((HumanEntity) npc.getEntity()).getEquipment().setItemInHand(caster.getInventory().getItemInMainHand());
		caster.teleport(caster.getLocation().add(new Vector(0,2,0)));
		caster.setGameMode(GameMode.SPECTATOR);
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
