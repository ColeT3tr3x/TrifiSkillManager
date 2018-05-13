package facejup.skillpack.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.main.CommandManager;
import facejup.skillpack.skills.ScrollType;
import facejup.skillpack.skills.skillshots.Displacement;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.FancyTextUtil;
import facejup.skillpack.util.Lang;
import facejup.skillpack.util.Numbers;
import facejup.skillpack.util.SkillUtil;
import net.md_5.bungee.api.chat.TextComponent;

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
			Player player = (Player) sender;
			User user = cm.getMain().getUserManager().getUser(player);
			if(args.length == 0)
			{
				List<Skill> skills = user.getSkills();
				if(skills.isEmpty())
				{
					player.sendMessage(Chat.translate(Lang.tag + "&bYou don't have any skills."));
					return true;
				}
				TextComponent premsg = new TextComponent(Chat.translate("&7Your Skills: "));
				for(Skill skill : skills)
				{
					TextComponent text = FancyTextUtil.getSkillItemTooltipMessage(skill, user.getSkillLevel(skill));
					if(!skill.equals(skills.get(skills.size()-1)))
					text.addExtra(", ");
					premsg.addExtra(text);
				}
				player.spigot().sendMessage(premsg);
				return true;
			}
			if(args[0].equalsIgnoreCase("menu"))
			{
				player.openInventory(getSkillsInventory((Player) sender));
				cm.getMain().getEventManager().getSkillMenuListener().menuPlayers.add(player);
				return true;
			}
			if(args[0].equalsIgnoreCase("info"))
			{
				if(args.length == 1)
				{
					List<Skill> skills = new ArrayList<>();
					skills.addAll(SkillAPI.getSkills().values());
					TextComponent premsg = new TextComponent(Chat.translate("&7All Skills: "));
					for(Skill skill : skills)
					{
						TextComponent text = FancyTextUtil.getSkillItemTooltipMessage(skill, 1);
						if(!skill.equals(skills.get(skills.size()-1)))
						text.addExtra(", ");
						premsg.addExtra(text);
					}
					player.spigot().sendMessage(premsg);
					return true;
				}
				if(SkillAPI.getSkill(args[1]) == null)
				{
					player.sendMessage(Lang.nullSkill);
					return true;
				}
				Skill skill = SkillAPI.getSkill(args[1]);
				player.sendMessage(Chat.translate("&b&l" + Chat.formatName(skill.getName())));
				for(String loreline : SkillUtil.getSkillItemStack(skill, user.hasSkill(skill, 1)?user.getSkillLevel(skill):1).getItemMeta().getLore())
				{
					player.sendMessage(loreline);
				}
			}
			if(args[0].equalsIgnoreCase("scroll") && player.isOp())
			{
				if(args.length == 1 || args.length == 2)
				{
					player.sendMessage(Lang.invalidArgs);
					return true;
				}
				if(ScrollType.getScrollType(args[1]) == null)
				{
					player.sendMessage(Chat.translate(Lang.tag + "Must specify a scroll type: Learn/Cast"));
					return true;
				}
				if(SkillAPI.getSkill(args[2]) == null)
				{
					player.sendMessage(Lang.nullSkill);
					return true;
				}
				ScrollType type = ScrollType.getScrollType(args[1]);
				Skill skill = SkillAPI.getSkill(args[2]);
				int level = 1;
				if(args.length > 3 && Numbers.isInt(args[3]))
					level = Integer.parseInt(args[3]);
				int uses = -1;
				if(args.length == 5 && Numbers.isInt(args[4]))
					uses = Integer.parseInt(args[4]);
				player.getInventory().addItem(SkillUtil.getSkillScroll(skill, level, type, uses));
			}
			if(args[0].equalsIgnoreCase("item") && player.isOp())
			{
				if(args.length == 1)
				{
					player.sendMessage(Lang.invalidArgs);
					return true;
				}
				if(SkillAPI.getSkill(args[1]) == null)
				{
					player.sendMessage(Lang.nullSkill);
					return true;
				}
				if(player.getInventory().getItemInMainHand() == null)
				{
					player.sendMessage(Lang.nullItem);
					return true;
				}
				ItemStack item = player.getInventory().getItemInMainHand();
				Skill skill = SkillAPI.getSkill(args[1]);
				int level = 1;
				if(args.length == 3 && Numbers.isInt(args[2]))
					level = Integer.parseInt(args[2]);
				player.getInventory().setItemInMainHand(SkillUtil.addSkillToItem(item, skill, level));
			}
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
