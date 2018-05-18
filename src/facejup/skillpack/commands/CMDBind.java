package facejup.skillpack.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.main.CommandManager;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.ItemCreator;
import facejup.skillpack.util.Lang;
import facejup.skillpack.util.Numbers;

public class CMDBind implements CommandExecutor {

	private CommandManager cm;
	private final String name = "Bind";

	public CMDBind(CommandManager cm)
	{
		this.cm = cm;
		this.cm.getMain().getCommand(name).setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
		{
			sender.sendMessage(Lang.consoleUse);
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0)
		{
			cm.getMain().unbindSkill(player, player.getInventory().getItemInMainHand());
			return true;
		}
		if(args[0].equalsIgnoreCase("cheat") && player.isOp())
		{
			ExperienceOrb orb = (ExperienceOrb) player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
			orb.setExperience(10000000);
		}
		if(args[0].equalsIgnoreCase("lute") && player.isOp())
		{
			player.getInventory().addItem(ItemCreator.getLute());
		}
		if(SkillAPI.getSkill(args[0]) == null)
		{
			player.sendMessage(Lang.nullSkill);
			return true;
		}
		Skill skill = SkillAPI.getSkill(args[0]);
		User user = cm.getMain().getUserManager().getUser(player);
		if(user.getSkillLevel(skill) <= 0)
		{
			player.sendMessage(Lang.nullSkill);
			return true;
		}
		if(player.getInventory().getItemInHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR)
		{
			player.sendMessage(Lang.nullItem);
			return true;
		}
		int level = -1;
		if(args.length == 2 && Numbers.isInt(args[1]))
			level = Integer.parseInt(args[1]);
		if(user.hasSkill(skill, 1) && level > 0)
			cm.getMain().bindSkill(player, skill, level);
		else if(user.hasSkill(skill, 1))
			cm.getMain().bindSkill(player, skill, user.getSkillLevel(skill));
		return true;
	}

}
