package facejup.skillpack.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class Chat {
	
	public static String translate(String str)
	{
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	public static String formatName(String str)
	{
		return StringUtils.capitaliseAllWords(str.toLowerCase().replaceAll("_", " "));
	}
	
	public static String formatItemName(ItemStack item)
	{
		if(item.getItemMeta().hasDisplayName())
		{
			return formatName(item.getItemMeta().getDisplayName());
		}
		return formatName(item.getType().toString());
	}

}
