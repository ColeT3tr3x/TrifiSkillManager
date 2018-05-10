package facejup.skillpack.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.main.CommandManager;
import facejup.skillpack.skills.ScrollType;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;
import facejup.skillpack.util.Numbers;
import facejup.skillpack.util.SkillUtil;
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
			if(args.length == 1 || args.length == 2)
			{
				sender.sendMessage(Lang.invalidArgs);
				return true;
			}
			if(args[1].equalsIgnoreCase("skill"))
			{
				if(SkillAPI.getSkill(args[2]) == null)
				{
					sender.sendMessage(Lang.nullSkill);
					return true;
				}
				Skill skill = SkillAPI.getSkill(args[2]);
				if(!cm.getMain().getNPCManager().shops.containsKey(npc))
				{
					player.sendMessage(Lang.nullShop);
					return true;
				}
				int level = 1;
				if(args.length > 3)
				{
					if(Numbers.isInt(args[3]))
						level = Integer.parseInt(args[3]);
				}
				if(skill.getCost(level) == 0)
				{
					player.sendMessage(Chat.translate(Lang.tag + "That skill isn't purchaseable"));
					return true;
				}
				cm.getMain().getNPCManager().shops.get(npc).addSkill(skill, level);
			}
			if(args[1].equalsIgnoreCase("item"))
			{
				if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR)
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must be holding an item."));
					return true;
				}
				if(!cm.getMain().getNPCManager().shops.containsKey(npc))
				{
					player.sendMessage(Lang.nullShop);
					return true;
				}
				ItemStack item = player.getInventory().getItemInMainHand();
				if(args.length == 2)
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must specify a price"));
					return true;
				}
				if(!Numbers.isInt(args[2]))
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must input a number for the price"));
					return true;
				}
				int price = Integer.parseInt(args[2]);
				item = item.clone();
				ItemMeta meta = item.getItemMeta();
				List<String> lore = (meta.hasLore()?meta.getLore():new ArrayList<>());
				meta.setDisplayName((meta.hasDisplayName()?ChatColor.AQUA + "" + item.getAmount() + " " + meta.getDisplayName():ChatColor.GOLD + "" + item.getAmount() + " " +  Chat.formatItemName(item)));
				lore.add(Chat.translate("&6Cost: " + price + " Coins"));
				meta.setLore(lore);
				item.setItemMeta(meta);
				cm.getMain().getNPCManager().shops.get(npc).addItem(item);

			}
			if(args[1].equalsIgnoreCase("scroll"))
			{
				if(args.length == 2 || args.length == 3 || args.length == 4)
				{
					player.sendMessage(Lang.invalidArgs);
					return true;
				}
				if(ScrollType.getScrollType(args[2]) == null)
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must specify a scroll type: Learn/Cast"));
					return true;
				}
				if(SkillAPI.getSkill(args[3]) == null)
				{
					player.sendMessage(Lang.nullSkill);
					return true;
				}
				ScrollType type = ScrollType.getScrollType(args[2]);
				Skill skill = SkillAPI.getSkill(args[3]);
				int level = 1;
				int cost = 0;
				if(Numbers.isInt(args[4]))
					cost = Integer.parseInt(args[4]);
				else
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must input a cost. Syntax: /shop add scroll (type) (skill) (cost) (level)"));
					return true;
				}
				if(args.length == 6 && Numbers.isInt(args[5]))
					level = Integer.parseInt(args[5]);
				ItemStack item = SkillUtil.getSkillScroll(skill, level, type);
				item = item.clone();
				ItemMeta meta = item.getItemMeta();
				List<String> lore = (meta.hasLore()?meta.getLore():new ArrayList<>());
				meta.setDisplayName((meta.hasDisplayName()?ChatColor.AQUA + "" + item.getAmount() + " " + meta.getDisplayName():ChatColor.GOLD + "" + item.getAmount() + " " +  Chat.formatItemName(item)));
				lore.add(Chat.translate("&6Cost: " + cost + " Coins"));
				meta.setLore(lore);
				item.setItemMeta(meta);
				cm.getMain().getNPCManager().shops.get(npc).addItem(item);
			}
		}
		return true;
	}

}
