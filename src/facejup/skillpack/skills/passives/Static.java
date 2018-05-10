package facejup.skillpack.skills.passives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;

public class Static extends Skill implements PassiveSkill {

	private HashMap<Player, Integer> cooldowns = new HashMap<>();
	private List<Player> using = new ArrayList<>();

	private final int COOLDOWN = 15;

	public Static(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
	}

	@Override
	public void initialize(LivingEntity player, int level) {
		cooldowns.put((Player) player, level);
		if(using.contains(player))
		{
			((Player)player).sendMessage(Chat.translate(Lang.tag + "Static toggled off"));
			using.remove(player);
		}
		else
		{
			((Player)player).sendMessage(Chat.translate(Lang.tag + "Static toggled on"));
			using.add((Player) player);
		}
	}

	@Override
	public void update(LivingEntity player, int level, int arg2) {
		if(!using.contains(player))
			return;
		if(!cooldowns.containsKey((Player) player))
			cooldowns.put((Player) player, level);
		cooldowns.put((Player) player, cooldowns.get(player)+level); 
		if(cooldowns.get(player) >= COOLDOWN)
		{
			cooldowns.put((Player) player, 0);
			Location loc = player.getLocation();
			if(loc.getWorld().getNearbyEntities(loc, level*2+3, level*2+3, level*2+3).size() > 1)
				for(Entity ent : loc.getWorld().getNearbyEntities(loc, level*2+3, level*2+3, level*2+3))
				{
					if(ent instanceof Damageable && !ent.equals(player))
					{
						ent.getWorld().strikeLightningEffect(ent.getLocation());
						((Damageable)ent).damage(4);
						return;
					}
				}
		}
	}

	@Override
	public void stopEffects(LivingEntity arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
