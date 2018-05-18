package facejup.skillpack.skills.skillshots;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;

import facejup.skillpack.main.Main;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.ItemCreator;
import facejup.skillpack.util.Lang;

public class Lullaby extends Skill implements SkillShot{

	private HashMap<LivingEntity, Long> cooldown = new HashMap<>();

	private final double COOLDOWN = 5;
	private final double MANACOST = 25;
	private final int COST = 7;
	private final int COST_SCALE = 2;

	public Lullaby(String name, String type, Material indicator, int maxLevel) {
		super(name, type, indicator, maxLevel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean cast(LivingEntity caster, int level) {
		if(!(caster instanceof Player))
			return false;
		if(this.isOnCooldown(caster, level))
		{
			DecimalFormat format = new DecimalFormat("##.##");
			caster.sendMessage("Can't cast for " + String.format("" + ((cooldown.get(caster)+this.getCooldown(level)*1000-System.currentTimeMillis())/1000.0), format) + " seconds.");
			return false;
		}
		User user = Main.getPlugin(Main.class).getUserManager().getUser((OfflinePlayer)caster);
		if(!user.decMana((int) MANACOST))
		{
			caster.sendMessage(Lang.tag + Chat.translate("&5Not enough mana! &c(" + user.getMana() + "/" + MANACOST + ")"));
			return false;
		}
		Player player = (Player) caster;
		if(player.getInventory().getItemInMainHand() == null || !player.getInventory().getItemInMainHand().isSimilar(ItemCreator.getLute()))
		{
			caster.sendMessage(Lang.tag + Chat.translate("&5Must be holding a Lute"));
			return false;
		}
		cooldown.put(caster, System.currentTimeMillis());
		playSong(player, level, 0);
		return true;
	}

	public void playSong(Player caster, int level, int tune)
	{
		for(Entity ent : caster.getWorld().getNearbyEntities(caster.getLocation(), level*2+5, level*2+5, level*2+5))
		{
			if(ent instanceof Player)
			{
				if(((Player)ent).getHealth() < ((Player)ent).getMaxHealth())
					((Player)ent).setHealth(((Player)ent).getHealth()+1);
			}
		}
		Long delay = 3L;
		switch(tune)
		{
		case 2:
			delay = 2L;
			caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_NOTE_GUITAR, 1, 1.1f);
			break;
		case 1:
			delay = 2L;
			caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_NOTE_GUITAR, 1, 1.2f);
		case 0:
			delay = 7L;
			caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_NOTE_GUITAR, 1, 1);
			break;
		case 3:
			delay = 7L;
			caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_NOTE_GUITAR, 1, 1.5f);
			break;
		case 4:
			delay = 10L;
			caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_NOTE_GUITAR, 1, 1.3f);
			break;
		case 5:
			caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_NOTE_GUITAR, 1, 1);
			break;
		case 6:
			caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_NOTE_GUITAR, 1, 0.9f);
			break;
		case 7:
			caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_NOTE_GUITAR, 1, 1);
			break;
		}
		Main main = Main.getPlugin(Main.class);
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
		{
			public void run()
			{
				playSong(caster, level, tune+1);
			}
		}, delay);
	}

	public boolean isOnCooldown(LivingEntity shooter, int level)
	{
		if(cooldown.containsKey(shooter))
			return (cooldown.get(shooter)+this.getCooldown(level)*1000 > System.currentTimeMillis());
		return false;

	}

	@Override
	public int getCost(int level)
	{
		return COST + level*COST_SCALE;
	}


}
