package facejup.skillpack.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.util.Chat;
import facejup.skillpack.util.SkillUtil;
import net.citizensnpcs.api.npc.NPC;

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

	public org.bukkit.inventory.Inventory getShopInventory()
	{
		List<Pair<Skill, Integer>> skills = getSkills();
		int invSize = (skills.isEmpty()?9:9*( (int) ((skills.size()-1)/9.0+1)));
		org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, invSize, npc.getName());
		for(Pair<Skill, Integer> skill : skills)
		{
			ItemStack item = SkillUtil.getSkillItemStack(skill.getLeft(), skill.getRight());
			List<String> lore = (item.getItemMeta().hasLore()?item.getItemMeta().getLore():new ArrayList<>());
			if(skill.getLeft().getCost(skill.getRight()) > 0)
			{
				lore.add(Chat.translate("&6&oCost: " + skill.getLeft().getCost(skill.getRight()) + " Skill Points"));
			}
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.addItem(item);
		}
		return inv;
	}


}
