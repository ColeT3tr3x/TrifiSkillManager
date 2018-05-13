package facejup.skillpack.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.skills.PaymentType;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Numbers;
import facejup.skillpack.util.SkillUtil;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;

public class Shop {

	private ConfigurationSection section;
	private NPC npc;
	private NPCManager npcm;

	public Shop(NPCManager npcm, NPC npc)
	{
		this.npc = npc;
		this.npcm = npcm;
		if(npcm.getFileControl().getConfig().contains("Shops." + npc.getId()))
		{
			section = npcm.getFileControl().getConfig().getConfigurationSection("Shops." + npc.getId());
		}
		else
		{
			npcm.getFileControl().getConfig().set("Shops." + npc.getId() + ".NPCName", npc.getName());
			section = npcm.getFileControl().getConfig().getConfigurationSection("Shops." + npc.getId());
			npcm.getFileControl().save();
		}
	}

	public void addSkill(Skill skill, int level)
	{
		if(!section.contains("Skills." + skill.getName()))
		{
			String key = skill.getName();
			section.set("Skills." + key + ".Name", key);
			section.set("Skills." + key + ".Level", level);
			npcm.getFileControl().save();
		}
	}
	
	public void addItem(ItemStack item)
	{
		if(!section.contains("Items"))
		{
			section.set("Items", Arrays.asList(item));
		}
		else
		{
			if(item == null)
				return;
			List<ItemStack> items = new ArrayList<>();
			for(Object o : section.getList("Items"))
			{
				if(o instanceof ItemStack)
				{
					items.add((ItemStack) o);
				}
			}
			items.add(item);
			section.set("Items", items);
		}
		npcm.getFileControl().save();
	}

	private List<Pair<Skill,Integer>> getSkills()
	{
		if(!section.contains("Skills"))
		{
			return new ArrayList<>();
		}
		List<Pair<Skill,Integer>> skills = new ArrayList<>();
		for(String key : section.getConfigurationSection("Skills").getKeys(false))
		{
			int level = 1;
			if(section.contains("Skills." + key + ".Level"))
				level = section.getInt("Skills." + key + ".Level");
			if(!section.contains("Skills." + key + ".Name"))
				continue;
			if(SkillAPI.getSkill(section.getString("Skills." + key + ".Name")) == null)
				continue;
			skills.add(Pair.of(SkillAPI.getSkill(section.getString("Skills." + key + ".Name")), level));
		}
		return skills;
	}
	private List<ItemStack> getItems()
	{
		if(!section.contains("Items") || section.getList("Items").isEmpty())
			return new ArrayList<>();
		List<ItemStack> items = new ArrayList<>();
		for(ItemStack item : (List<ItemStack>) section.getList("Items"))
		{
			items.add(item);
		}
		return items;
	}

	public org.bukkit.inventory.Inventory getShopInventory(Player player)
	{
		User user = npcm.getMain().getUserManager().getUser(player);
		List<Pair<Skill, Integer>> skills = getSkills();
		int invSize = skills.size() + getItems().size()-1;
		invSize = skills.isEmpty() && getItems().isEmpty()?9:9*( (int) ((getItems().size() + skills.size()-1)/9.0+1));
		org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, invSize, npc.getName());
		for(Pair<Skill, Integer> skill : skills)
		{
			ItemStack item = SkillUtil.getSkillItemStack(skill.getLeft(), skill.getRight());
			List<String> lore = (item.getItemMeta().hasLore()?item.getItemMeta().getLore():new ArrayList<>());
			if(skill.getLeft().getCost(skill.getRight()) > 0)
			{
				lore.add(Chat.translate("&6&oCost: " + skill.getLeft().getCost(skill.getRight()) + " Skill Points"));
			}
			lore.add(Chat.translate("&bYou have " + user.getSkillpoints() + " Skillpoints"));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(Chat.translate("&r&5Skill: " + (meta.hasDisplayName()?meta.getDisplayName():Chat.formatItemName(item))));
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.setItem(inv.firstEmpty(), item);
		}
		for(ItemStack itemtest : getItems())
		{
			ItemStack item = itemtest.clone();
			ItemMeta meta = item.getItemMeta();
			List<String> lore = (meta.hasLore()?meta.getLore():new ArrayList<>());
			if(lore.size() == 0)
				continue;
			String type = lore.get(lore.size()-1);
			if(!ChatColor.stripColor(type).startsWith("Cost: "))
				continue;
			PaymentType paytype = PaymentType.getPaymentByName(type.substring(type.lastIndexOf(" ")+1,type.length()-1));
			if(paytype == null)
				continue;
			lore.add(Chat.translate("&r&5You have " + (paytype==PaymentType.COIN?npcm.getMain().getEconomy().getBalance(player):user.getSkillpoints()) + " " + Chat.formatName(paytype.name()) + "s"));
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.setItem(inv.firstEmpty(), item);
		}
		return inv;
	}


}
