package facejup.skillpack.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.skills.ScrollType;

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
	
	public static ItemStack getSkillScroll(Skill skill, int level, ScrollType type)
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
		item.setItemMeta(meta);
		return item;
	}
	

}
