package facejup.skillpack.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.api.skills.TargetSkill;

import facejup.skillpack.main.CommandManager;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Lang;

public class CMDCast implements CommandExecutor {

	private CommandManager cm;
	private final String name = "cast";

	public CMDCast(CommandManager cm)
	{
		this.cm = cm;
		cm.getMain().getCommand(name).setExecutor(this);
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
			sender.sendMessage(Lang.invalidArgs);
			return true;
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
		if(skill instanceof SkillShot)
			((SkillShot) skill).cast(player, user.getSkillLevel(skill));
		else if(skill instanceof PassiveSkill)
			((PassiveSkill) skill).initialize(player, user.getSkillLevel(skill));
		else
		{
			if(args.length == 1)
			{
				sender.sendMessage(Lang.invalidArgs);
				return true;
			}
			if(!Bukkit.getOfflinePlayer(args[1]).isOnline())
			{
				sender.sendMessage(Lang.nullPlayer);
				return true;
			}
			Player target = Bukkit.getPlayer(args[1]);
			((TargetSkill) skill).cast(player, target, user.getSkillLevel(skill), false);
		}
		return true;
	}


}
