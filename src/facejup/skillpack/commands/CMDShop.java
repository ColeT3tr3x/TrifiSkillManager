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
import facejup.skillpack.skills.PaymentType;
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
			if(cm.getMain().getNPCManager().shops.containsKey(npc))
			{
				player.sendMessage(Chat.translate(Lang.tag + npc.getName() + " already has a shop"));
				return true;
			}
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
				if(args.length == 2 || PaymentType.getPaymentByName(args[2]) == null)
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must specify a payment type: Coin, SkillPoint"));
					return true;
				}
				PaymentType type = PaymentType.getPaymentByName(args[2]);
				if(args.length == 3)
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must specify a cost"));
					return true;
				}
				if(!Numbers.isInt(args[3]))
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must input a number for the price"));
					return true;
				}
				int price = Integer.parseInt(args[3]);
				item = item.clone();
				ItemMeta meta = item.getItemMeta();
				List<String> lore = (meta.hasLore()?meta.getLore():new ArrayList<>());
				meta.setDisplayName((meta.hasDisplayName()?ChatColor.AQUA + "" + item.getAmount() + " " + meta.getDisplayName():ChatColor.GOLD + "" + item.getAmount() + " " +  Chat.formatItemName(item)));
				lore.add(Chat.translate("&6Cost: " + price + " " + Chat.formatName(type.name()) + "s"));
				meta.setLore(lore);
				item.setItemMeta(meta);
				cm.getMain().getNPCManager().shops.get(npc).addItem(item);	
				player.sendMessage(Chat.translate(Lang.tag + meta.getDisplayName() + " &aadded to &b" + npc.getName() + " &ashop for &6" + price + " " + Chat.formatName(type.name()) + "s"));

			}
			if(args[1].equalsIgnoreCase("scroll"))
			{
				if(!cm.getMain().getNPCManager().shops.containsKey(npc))
				{
					player.sendMessage(Lang.nullShop);
					return true;
				}
				if(args.length == 2 || ScrollType.getScrollType(args[2]) == null)
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must specify a scroll type: Learn/Cast"));
					return true;
				}
				ScrollType type = ScrollType.getScrollType(args[2]);
				if(args.length == 3 || SkillAPI.getSkill(args[3]) == null)
				{
					player.sendMessage(Lang.nullSkill);
					return true;
				}
				Skill skill = SkillAPI.getSkill(args[3]);
				if(args.length == 4 || !Numbers.isInt(args[4]))
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must input a number for the level"));
					return true;
				}
				int level = Integer.parseInt(args[4]);
				if(args.length == 5 || PaymentType.getPaymentByName(args[5]) == null)
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must specify a payment type: Coin, SkillPoint"));
					return true;
				}
				PaymentType paytype = PaymentType.getPaymentByName(args[5]);
				if(args.length == 6 || !Numbers.isInt(args[6]))
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must specify a cost"));
					return true;
				}
				int cost = Integer.parseInt(args[6]);
				int uses = 1;
				if(args.length == 8 && Numbers.isInt(args[7]))
					uses = Integer.parseInt(args[7]);
				ItemStack item = SkillUtil.getSkillScroll(skill, level, type, uses);
				item = item.clone();
				ItemMeta meta = item.getItemMeta();
				List<String> lore = (meta.hasLore()?meta.getLore():new ArrayList<>());
				meta.setDisplayName((meta.hasDisplayName()?ChatColor.AQUA + "" + item.getAmount() + " " + meta.getDisplayName():ChatColor.GOLD + "" + item.getAmount() + " " +  Chat.formatItemName(item)));
				lore.add(Chat.translate("&6Cost: " + cost + " " + Chat.formatName(paytype.name()) + "s"));
				meta.setLore(lore);
				item.setItemMeta(meta);
				cm.getMain().getNPCManager().shops.get(npc).addItem(item);
				player.sendMessage(Chat.translate(Lang.tag + meta.getDisplayName() + " &aadded to &b" + npc.getName() + " &ashop for &6" + cost + " " + Chat.formatName(paytype.name()) + "s"));
			}
		}
		return true;
	}

}
