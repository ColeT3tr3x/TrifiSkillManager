package facejup.skillpack.util;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.sucy.skill.api.skills.Skill;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class FancyTextUtil {

	public static TextComponent getSkillItemTooltipMessage(Skill skill, int level)
	{
		ItemStack item = SkillUtil.getSkillItemStack(skill, level);
		net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = new NBTTagCompound();
		nmsItemStack.save(compound);
		String json = compound.toString();
		BaseComponent[] hoverEventComponents = new BaseComponent[]{
				new TextComponent(json) // The only element of the hover events basecomponents is the item json
		};
		HoverEvent hover_event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);	
		ClickEvent click_event = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skills info " + skill.getName());		
		TextComponent component = new TextComponent(ChatColor.AQUA + skill.getName());
		component.setHoverEvent(hover_event);
		component.setClickEvent(click_event);
		return component;
	}
}
