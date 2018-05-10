package facejup.skillpack.skills.passives;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;

public class Weightless extends Skill implements PassiveSkill{

	public Weightless(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&7Take %LEVEL*20% percent damage");
		getDescription().add("&7reduction from explosives");
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
