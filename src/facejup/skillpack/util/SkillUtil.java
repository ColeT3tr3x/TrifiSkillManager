package facejup.skillpack.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.TargetSkill;

import facejup.skillpack.main.Main;
import facejup.skillpack.skills.IPlayerTarget;
import facejup.skillpack.skills.ScrollType;
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
			if(skill instanceof TargetSkill && skill instanceof IPlayerTarget)
			{
				lore.add(ChatColor.DARK_BLUE + "Targettable: Players");
			}
			else if(skill instanceof TargetSkill)
			{
				lore.add(ChatColor.DARK_BLUE + "Targettable: Any");
			}
			else
			{
				lore.add(ChatColor.GRAY + skill.getType());
			}
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
		if(!item.hasItemMeta())
			return false;
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
	
	public static boolean isTargettingPlayer(Player player, double range)
	{
		Location loc = player.getLocation().clone();
		for(Entity ent : loc.getWorld().getNearbyEntities(loc, range, range, range))
		{
			if(ent.equals(player))
				continue;
			if(!(ent instanceof Player))
				continue;
			Vector dir = ent.getLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
			double dot = dir.dot(player.getLocation().getDirection());
			if(dot > 0.98)
				return true;
			dir = ((LivingEntity)ent).getEyeLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
			dot = dir.dot(player.getLocation().getDirection());
			if(dot > 0.98)
				return true;
		}
		return false;
	}
	
	public static boolean isTargettingEntity(Player player, double range)
	{
		Location loc = player.getLocation().clone();
		for(Entity ent : loc.getWorld().getNearbyEntities(loc, range, range, range))
		{
			if(ent.equals(player))
				continue;
			if(!(ent instanceof LivingEntity))
				continue;
			Vector dir = ent.getLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
			double dot = dir.dot(player.getLocation().getDirection());
			if(dot > 0.98)
				return true;
			dir = ((LivingEntity)ent).getEyeLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
			dot = dir.dot(player.getLocation().getDirection());
			if(dot > 0.98)
				return true;
		}
		return false;
	}
	
	public static LivingEntity getTarget(Player player, double range)
	{
		Location loc = player.getLocation().clone();
		double distance = range+1;
		LivingEntity returnent = null;
		for(Entity ent : loc.getWorld().getNearbyEntities(loc, range, range, range))
		{
			if(ent.equals(player))
				continue;
			if(!(ent instanceof LivingEntity))
				continue;
			Vector dir = ent.getLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
			double dot = dir.dot(player.getLocation().getDirection());
			if(dot > 0.98 && ent.getLocation().distance(loc) < distance)
			{
				distance = ent.getLocation().distance(loc);
				returnent = (LivingEntity)ent;
			}
			dir = ((LivingEntity)ent).getEyeLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
			dot = dir.dot(player.getLocation().getDirection());
			if(dot > 0.98 && ent.getLocation().distance(loc) < distance)
			{
				distance = ent.getLocation().distance(loc);
				returnent = (LivingEntity)ent;
			}
		}
		return returnent;
	}
	
	public static boolean hasPassive(Player player, PassiveSkill skill, int level)
	{
		Main main = Main.getPlugin(Main.class);
		if(main.getUserManager().getUser(player).hasSkill((Skill) skill, level))
			return true;
		if(player.getInventory().getItemInMainHand() != null && SkillUtil.itemHasSkill(player.getInventory().getItemInMainHand(), (Skill) skill, level))
			return true;
		if(player.getInventory().getHelmet() != null && SkillUtil.itemHasSkill(player.getInventory().getHelmet(), (Skill) skill, level))
			return true;
		if(player.getInventory().getChestplate() != null && SkillUtil.itemHasSkill(player.getInventory().getChestplate(), (Skill) skill, level))
			return true;
		if(player.getInventory().getLeggings() != null && SkillUtil.itemHasSkill(player.getInventory().getLeggings(), (Skill) skill, level))
			return true;
		if(player.getInventory().getBoots() != null && SkillUtil.itemHasSkill(player.getInventory().getBoots(), (Skill) skill, level))
			return true;
		return false;
	}
	
	public static int getPassiveLevel(Player player, PassiveSkill skill)
	{
		Main main = Main.getPlugin(Main.class);
		int retlevel = 0;;
		if(main.getUserManager().getUser(player).hasSkill((Skill) skill, 1) && main.getUserManager().getUser(player).getSkillLevel((Skill)skill) > retlevel)
			retlevel = main.getUserManager().getUser(player).getSkillLevel((Skill)skill);
		if(player.getInventory().getItemInMainHand() != null && SkillUtil.itemHasSkill(player.getInventory().getItemInMainHand(), (Skill) skill, 1) && getItemSkillLevel(player.getInventory().getItemInMainHand(), (Skill) skill) > retlevel)
			retlevel = getItemSkillLevel(player.getInventory().getItemInMainHand(), (Skill) skill);
		if(player.getInventory().getHelmet() != null && SkillUtil.itemHasSkill(player.getInventory().getHelmet(), (Skill) skill, 1) && getItemSkillLevel(player.getInventory().getHelmet(), (Skill) skill) > retlevel)
			retlevel = getItemSkillLevel(player.getInventory().getHelmet(), (Skill) skill);
		if(player.getInventory().getChestplate() != null && SkillUtil.itemHasSkill(player.getInventory().getChestplate(), (Skill) skill, 1) && getItemSkillLevel(player.getInventory().getChestplate(), (Skill) skill) > retlevel)
			retlevel = getItemSkillLevel(player.getInventory().getChestplate(), (Skill) skill);
		if(player.getInventory().getLeggings() != null && SkillUtil.itemHasSkill(player.getInventory().getLeggings(), (Skill) skill, 1) && getItemSkillLevel(player.getInventory().getLeggings(), (Skill) skill) > retlevel)
			retlevel = getItemSkillLevel(player.getInventory().getLeggings(), (Skill) skill);
		if(player.getInventory().getBoots() != null && SkillUtil.itemHasSkill(player.getInventory().getBoots(), (Skill) skill, 1) && getItemSkillLevel(player.getInventory().getBoots(), (Skill) skill) > retlevel)
			retlevel = getItemSkillLevel(player.getInventory().getBoots(), (Skill) skill);
		return retlevel;
	}


}
