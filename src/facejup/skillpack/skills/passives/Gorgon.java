package facejup.skillpack.skills.passives;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;

public class Gorgon extends Skill implements PassiveSkill{

	public Gorgon(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		getDescription().add("&bPassive");
		getDescription().add("&7Slow players who look at you");
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
