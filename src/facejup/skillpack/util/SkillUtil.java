package facejup.skillpack.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.api.skills.Skill;

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

}
