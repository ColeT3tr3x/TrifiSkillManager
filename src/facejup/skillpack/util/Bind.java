package facejup.skillpack.util;

import org.bukkit.inventory.ItemStack;

import com.sucy.skill.api.skills.Skill;

public class Bind {
	
	public Skill skill;
	public ItemStack item;
	public int level;
	
	public Bind(Skill skill, ItemStack item, int level)
	{
		this.skill = skill;
		this.item = item;
		this.level = level;
	}

}
