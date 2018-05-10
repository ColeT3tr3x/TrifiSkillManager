package facejup.skillpack.skills.passives;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;

public class Thorns extends Skill implements PassiveSkill{

	public Thorns(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Deal %LEVEL*20% percent damage");
		getDescription().add("&7to the attacker when hit");
	}

	@Override
	public void initialize(LivingEntity arg0, int arg1) {
	}

	@Override
	public void stopEffects(LivingEntity arg0, int arg1) {
	}

	@Override
	public void update(LivingEntity arg0, int arg1, int arg2) {
	}

}
