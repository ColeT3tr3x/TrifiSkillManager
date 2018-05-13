package facejup.skillpack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import net.minecraft.server.v1_12_R1.NBTTagByte;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class ItemCreator{
	private ItemStack item;

	public ItemCreator(Material mat)
	{
		this.item = new ItemStack(mat, 1);
	}

	public ItemCreator(ItemStack item)
	{
		this.item = item.clone();
	}

	public ItemCreator setAmount(int i)
	{
		this.item.setAmount(i);
		return this;
	}

	public ItemCreator setDisplayname(String display)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Chat.translate(display));
		item.setItemMeta(meta);
		return this;
	}

	public ItemCreator setDyeColor(DyeColor color)
	{
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(color.getColor());
		item.setItemMeta(meta);
		return this;
	}

	public ItemCreator setLore(List<String> lore)
	{
		ItemMeta meta = item.getItemMeta();
		List<String> newLore = new ArrayList<>();
		for(String str : lore)
		{
			newLore.add(ChatColor.translateAlternateColorCodes('&', str));
		}
		meta.setLore(newLore);
		item.setItemMeta(meta);
		return this;
	}

	public ItemCreator setData(MaterialData data)
	{
		item.setData(data);
		return this;
	}

	public ItemCreator setData(int i)
	{
		item.setDurability((byte) i);
		return this;
	}

	public ItemCreator addEnchant(Enchantment ench, int i) {
		if(!item.containsEnchantment(ench))
			item.addUnsafeEnchantment(ench, i);
		return this;
	}

	public ItemCreator addGlowing()
	{
		if(getTag(item, "HideFlags") == 0)
			item = setTag(item, "HideFlags", 1);
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		return this;
	}

	public ItemCreator hideFlags(int i)
	{
		item = setTag(item, "HideFlags", i);
		return this;
	}

	public ItemCreator setPotionType(PotionEffect effect)
	{
		if(item.getItemMeta() instanceof PotionMeta)
		{
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			meta.addCustomEffect(effect, true);
			item.setItemMeta(meta);
		}
		return this;
	}


	public ItemStack getItem()
	{
		return this.item;
	}	

	private ItemStack setTag(ItemStack item, String tagname, int amt)
	{
		net.minecraft.server.v1_12_R1.ItemStack itemnms = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = (itemnms.hasTag() ? itemnms.getTag() : new NBTTagCompound());
		tag.set(tagname, new NBTTagByte((byte) amt));
		itemnms.setTag(tag);
		return CraftItemStack.asBukkitCopy(itemnms);
	}
	
	public ItemCreator addSkill(Skill skill, int level)
	{
		item = SkillUtil.addSkillToItem(item, skill, level);
		return this;
	}
	
	public ItemCreator addSkills(List<Skill> skills, int level)
	{
		skills.forEach(skill -> SkillUtil.addSkillToItem(item, skill, Numbers.getRandom(1, level>skill.getMaxLevel()?skill.getMaxLevel():level)));
		return this;
	}

	private byte getTag(ItemStack item, String tagname)
	{
		net.minecraft.server.v1_12_R1.ItemStack itemnms = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = (itemnms.hasTag() ? itemnms.getTag() : new NBTTagCompound());
		return tag.getByte(tagname);
	}
	public static ItemStack getUnidentified(int level)
	{
		ItemCreator creator = new ItemCreator(Material.STICK);
		return creator.setAmount(1)
		.setDisplayname(Chat.translate("&5Unidentified Weapon"))
		.setLore(Arrays.asList(Chat.translate("&bLevel " + level)))
		.getItem();
	}
	
	public static ItemStack getRandomWeapon()
	{
		int random = Numbers.getRandom(0, 24);
		switch(random)
		{
		case 0:
			return new ItemStack(Material.WOOD_SWORD);
		case 1:
			return new ItemStack(Material.STONE_SWORD);
		case 2:
			return new ItemStack(Material.IRON_SWORD);
		case 3:
			return new ItemStack(Material.GOLD_SWORD);
		case 4:
			return new ItemStack(Material.DIAMOND_SWORD);
		case 5:
			return new ItemStack(Material.WOOD_AXE);
		case 6:
			return new ItemStack(Material.STONE_AXE);
		case 7:
			return new ItemStack(Material.IRON_AXE);
		case 8:
			return new ItemStack(Material.GOLD_AXE);
		case 9:
			return new ItemStack(Material.DIAMOND_AXE);
		case 10:
			return new ItemStack(Material.WOOD_PICKAXE);
		case 11:
			return new ItemStack(Material.STONE_PICKAXE);
		case 12:
			return new ItemStack(Material.IRON_PICKAXE);
		case 13:
			return new ItemStack(Material.GOLD_PICKAXE);
		case 14:
			return new ItemStack(Material.DIAMOND_PICKAXE);
		case 15:
			return new ItemStack(Material.WOOD_SPADE);
		case 16:
			return new ItemStack(Material.STONE_SPADE);
		case 17:
			return new ItemStack(Material.IRON_SPADE);
		case 18:
			return new ItemStack(Material.GOLD_SPADE);
		case 19:
			return new ItemStack(Material.DIAMOND_SPADE);
		case 20:
			return new ItemStack(Material.WOOD_HOE);
		case 21:
			return new ItemStack(Material.STONE_HOE);
		case 22:
			return new ItemStack(Material.IRON_HOE);
		case 23:
			return new ItemStack(Material.GOLD_HOE);
		case 24:
			return new ItemStack(Material.DIAMOND_HOE);
		}
		return new ItemStack(Material.WOOD_SWORD);
	}
	public static double getArmorPoints(ItemStack item) {
		double red = 0.0;
		//
		if(item.getType().toString().contains("HELMET")) {
			if (item.getType() == Material.LEATHER_HELMET) red = red + 0.04;
			else if (item.getType() == Material.GOLD_HELMET) red = red + 0.08;
			else if (item.getType() == Material.CHAINMAIL_HELMET) red = red + 0.08;
			else if (item.getType() == Material.IRON_HELMET) red = red + 0.08;
			else if (item.getType() == Material.DIAMOND_HELMET) red = red + 0.12;
		}
		//
		if(item.getType().toString().contains("CHESTPLATE")) {
			if (item.getType() == Material.LEATHER_CHESTPLATE)    red = red + 0.12;
			else if (item.getType() == Material.GOLD_CHESTPLATE)red = red + 0.20;
			else if (item.getType() == Material.CHAINMAIL_CHESTPLATE) red = red + 0.20;
			else if (item.getType() == Material.IRON_CHESTPLATE) red = red + 0.24;
			else if (item.getType() == Material.DIAMOND_CHESTPLATE) red = red + 0.32;
		}
		//
		if(item.getType().toString().contains("LEGGINGS")) {
			if (item.getType() == Material.LEATHER_LEGGINGS) red = red + 0.08;
			else if (item.getType() == Material.GOLD_LEGGINGS)    red = red + 0.12;
			else if (item.getType() == Material.CHAINMAIL_LEGGINGS) red = red + 0.16;
			else if (item.getType() == Material.IRON_LEGGINGS)    red = red + 0.20;
			else if (item.getType() == Material.DIAMOND_LEGGINGS) red = red + 0.24;
		}
		//
		if(item.getType().toString().contains("BOOTS")) {
			if (item.getType() == Material.LEATHER_BOOTS) red = red + 0.04;
			else if (item.getType() == Material.GOLD_BOOTS) red = red + 0.04;
			else if (item.getType() == Material.CHAINMAIL_BOOTS) red = red + 0.04;
			else if (item.getType() == Material.IRON_BOOTS) red = red + 0.08;
			else if (item.getType() == Material.DIAMOND_BOOTS)    red = red + 0.12;
		}
		//
		return red * (1/0.04);
	}
}