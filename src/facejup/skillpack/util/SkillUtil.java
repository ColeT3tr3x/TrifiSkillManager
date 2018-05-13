package facejup.skillpack.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;

import facejup.skillpack.skills.ScrollType;
import facejup.skillpack.users.User;
import net.md_5.bungee.api.ChatColor;

public class SkillUtil {

	public static ItemStack getSkillItemStack(Skill skill, int level)
	{
		ItemStack item = new ItemStack(Material.AIR, 1);
		if(skill.getIndicator() != null)
		{
			item = new ItemStack(skill.getIndicator().getType(), level);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(Chat.translate("&b" + skill.getName()));
			meta.setDisplayName(meta.getDisplayName() + Chat.translate(" &d(" + level + "/" + skill.getMaxLevel() + ")"));
			List<String> lore = (meta.hasLore()?meta.getLore():new ArrayList<>());
			for(String str : skill.getDescription())
			{
				if(str.contains("LEVEL*"))
				{
					String newstr = str.substring(0, str.indexOf("%"));
					newstr += "&d(";
					newstr+="" + level*Integer.parseInt(str.substring(str.indexOf("%")+7, str.lastIndexOf("%")));
					newstr += "&d)";
					newstr += "&7" + str.substring(str.lastIndexOf("%")+1, str.length());
					str = newstr;
				}
				lore.add(Chat.translate(str.replaceAll("%LEVEL%", level + "")));
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}
	
	public static int getItemSkillLevel(ItemStack item, Skill skill)
	{
		if(!itemHasSkill(item, skill, 1))
			return 0;
		for(String loreline : item.getItemMeta().getLore())
		{
			loreline = ChatColor.stripColor(loreline);
			if(!loreline.contains(" "))
				continue;
			if(SkillAPI.getSkill(loreline.substring(0, loreline.indexOf(" "))) != null)
			{
				Skill tempskill = SkillAPI.getSkill(loreline.substring(0, loreline.indexOf(" ")));
				int templevel = 1;
				if(Numbers.isInt(loreline.substring(loreline.indexOf(" ")+1)))
					templevel = Integer.parseInt(loreline.substring(loreline.indexOf(" ")+1));
				if(tempskill.equals(skill))
					return templevel;
			}
		}
		return 0;
	}

	public static boolean itemHasSkill(ItemStack item, Skill skill, int level)
	{

		if(!item.getItemMeta().hasLore())
			return false;
		for(String loreline : item.getItemMeta().getLore())
		{
			loreline = ChatColor.stripColor(loreline);
			if(!loreline.contains(" "))
				continue;
			if(SkillAPI.getSkill(loreline.substring(0, loreline.indexOf(" "))) != null)
			{
				Skill tempskill = SkillAPI.getSkill(loreline.substring(0, loreline.indexOf(" ")));
				int templevel = 1;
				if(Numbers.isInt(loreline.substring(loreline.indexOf(" ")+1)))
					templevel = Integer.parseInt(loreline.substring(loreline.indexOf(" ")+1));
				if(tempskill.equals(skill) && templevel >= level)
					return true;
			}
		}
		return false;
	}

	public static ItemStack addSkillToItem(ItemStack item, Skill skill, int level)
	{
		ItemMeta meta = item.getItemMeta();
		List<String> lore = (meta.hasLore()?meta.getLore():new ArrayList<>());
		lore.add(ChatColor.GREEN + skill.getName() + " " + level);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getSkillScroll(Skill skill, int level, ScrollType type, int uses)
	{
		ItemStack item = new ItemStack(Material.PAPER);
		String name = Chat.translate("&bScroll: " + Chat.formatName(type.toString()) + " " + skill.getName() + " &5(" + level + "/" + skill.getMaxLevel() + ")");
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);		
		List<String> lore = (meta.hasLore()?meta.getLore():new ArrayList<>());
		for(String str : skill.getDescription())
		{
			if(str.contains("LEVEL*"))
			{
				String newstr = str.substring(0, str.indexOf("%"));
				newstr += "&d(";
				newstr+="" + level*Integer.parseInt(str.substring(str.indexOf("%")+7, str.lastIndexOf("%")));
				newstr += "&d)";
				newstr += "&7" + str.substring(str.lastIndexOf("%")+1, str.length());
				str = newstr;
			}
			lore.add(Chat.translate(str.replaceAll("%LEVEL%", level + "")));
		}
		if(uses > 0)
			lore.add(ChatColor.AQUA + "Uses: " + uses);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}


}
