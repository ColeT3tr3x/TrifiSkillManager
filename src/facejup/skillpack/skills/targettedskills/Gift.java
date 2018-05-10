package facejup.skillpack.skills.targettedskills;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.TargetSkill;

import facejup.skillpack.main.Main;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;

public class Gift extends Skill implements TargetSkill{

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();

	private final double COOLDOWN = 0.1;
	private final double MANACOST = 20;

	public Gift(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Send a player an item");
	}

	public boolean isOnCooldown(LivingEntity shooter, int level)
	{
		if(cooldown.containsKey(shooter))
			return (cooldown.get(shooter)+this.getCooldown(level)*1000 > System.currentTimeMillis());
		return false;
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
		if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR)
		{
			player.sendMessage(Chat.translate(Lang.tag + "Must be holding an item."));
			return false;
		}
		ItemStack item = player.getInventory().getItemInMainHand();
		player.getInventory().setItemInMainHand(null);
		player2.getInventory().addItem(item);
		player.sendMessage(Chat.translate(Lang.tag + "You sent " + player2.getName() + " " + item.getAmount() + " " + Chat.formatItemName(item)));
		player2.sendMessage(Chat.translate(Lang.tag + player.getName() + " sent you " + item.getAmount() + " " + Chat.formatItemName(item)));
		return true;
	}

}
