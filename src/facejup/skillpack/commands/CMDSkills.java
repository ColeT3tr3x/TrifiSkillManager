package facejup.skillpack.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.main.CommandManager;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;

public class CMDSkills implements CommandExecutor {

	private CommandManager cm;
	private final String name = "skills";

	public CMDSkills(CommandManager cm)
	{
		this.cm = cm;
		this.cm.getMain().getCommand(name).setExecutor(this);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player)
		{
			((Player) sender).openInventory(getSkillsInventory((Player) sender));
			cm.getMain().getEventManager().getSkillMenuListener().menuPlayers.add((Player) sender);
		}
		return true;
	}

	public Inventory getSkillsInventory(Player player)
	{
		List<Skill> skills = cm.getMain().getUserManager().getUser(player).getSkills();
		Inventory inv = Bukkit.createInventory(player, 9);
		User user = cm.getMain().getUserManager().getUser(player);
		if(!skills.isEmpty())
		{
			inv = Bukkit.createInventory(player, 9*( (int) ((skills.size()-1)/9.0+1)));
			int slot = 0;
			for(Skill skill : skills)
			{
				if(skill.getIndicator() != null)
				{
					ItemStack item = new ItemStack(skill.getIndicator().getType(), user.getSkillLevel(skill));
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(Chat.translate("&b" + skill.getName()));
					meta.setDisplayName(meta.getDisplayName() + Chat.translate(" &d(" + user.getSkillLevel(skill) + "/" + skill.getMaxLevel() + ")"));
					List<String> lore = (meta.hasLore()?meta.getLore():new ArrayList<>());
					for(String str : skill.getDescription())
					{
						int level = user.getSkillLevel(skill);
						if(str.contains("LEVEL*"))
						{
							String newstr = str.substring(0, str.indexOf("%"));
							newstr += "&d(";
							newstr+="" + level*Integer.parseInt(str.substring(str.indexOf("%")+7, str.lastIndexOf("%")));
							newstr += "&d)";
							newstr += "&7" + str.substring(str.lastIndexOf("%")+1, str.length());
							str = newstr;
						}
						lore.add(Chat.translate(str.replaceAll("%LEVEL%", user.getSkillLevel(skill) + "")));
					}
					lore.add(Chat.translate("&5You have " + user.getSkillpoints() + " skill points"));
					if(user.getSkillpoints() > 0 && user.getSkillLevel(skill) < skill.getMaxLevel())
						lore.add(Chat.translate("&aClick to level up!"));
					meta.setLore(lore);
					item.setItemMeta(meta);
					inv.setItem(slot, item);
					slot++;
				}
			}
		}
		return inv;
	}

}
