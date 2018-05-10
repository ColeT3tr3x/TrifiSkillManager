package facejup.skillpack.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.main.CommandManager;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;
import facejup.skillpack.util.Numbers;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class CMDShop implements CommandExecutor {
	
	private CommandManager cm;
	private final String name = "shop";
	
	public CMDShop(CommandManager cm)
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
		if(args.length == 0)
		{
			sender.sendMessage(Lang.invalidArgs);
			return true;
		}
		Player player = (Player) sender;
		if(CitizensAPI.getDefaultNPCSelector().getSelected(player) == null)
		{
			sender.sendMessage(Chat.translate(Lang.tag + "You must select an npc"));
		}
		NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(player);
		if(args[0].equalsIgnoreCase("create"))
		{
			cm.getMain().getNPCManager().createShop(npc);
			player.sendMessage(Chat.translate(Lang.tag + "Shop created for " + npc.getName()));
		}
		else if(args[0].equalsIgnoreCase("add"))
		{
			if(args.length == 1)
			{
				sender.sendMessage(Lang.invalidArgs);
				return true;
			}
			if(SkillAPI.getSkill(args[1]) == null)
			{
				sender.sendMessage(Lang.nullSkill);
				return true;
			}
			Skill skill = SkillAPI.getSkill(args[1]);
			if(!cm.getMain().getNPCManager().shops.containsKey(npc))
			{
				player.sendMessage(Lang.nullShop);
				return true;
			}
			int level = 1;
			if(args.length > 2)
			{
				if(Numbers.isInt(args[2]))
					level = Integer.parseInt(args[2]);
			}
			if(skill.getCost(level) == 0)
			{
				player.sendMessage(Chat.translate(Lang.tag + "That skill isn't purchaseable"));
				return true;
			}
			cm.getMain().getNPCManager().shops.get(npc).addSkill(skill, 1);
		}
		return true;
	}

}
