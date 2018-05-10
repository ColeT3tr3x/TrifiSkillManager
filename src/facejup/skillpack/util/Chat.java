package facejup.skillpack.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PlayerConnection;

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
	
    public static void sendActionBar(Player player, String message) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        IChatBaseComponent chat = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chat, ChatMessageType.GAME_INFO);
        connection.sendPacket(packetPlayOutChat);
    }
    
}
