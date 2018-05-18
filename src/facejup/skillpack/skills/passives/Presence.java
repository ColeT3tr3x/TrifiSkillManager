package facejup.skillpack.skills.passives;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;

public class Presence extends Skill implements PassiveSkill {

	public Presence(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Feel the presence of nearby entities");
	}

	@Override
	public void initialize(LivingEntity arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopEffects(LivingEntity arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(LivingEntity arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
