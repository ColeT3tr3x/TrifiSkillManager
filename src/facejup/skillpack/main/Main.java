package facejup.skillpack.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.SkillPlugin;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.skills.passives.Static;
import facejup.skillpack.skills.skillshots.Displacement;
import facejup.skillpack.skills.skillshots.Drill;
import facejup.skillpack.skills.skillshots.Eruption;
import facejup.skillpack.skills.skillshots.FireLeap;
import facejup.skillpack.skills.skillshots.Fireball;
import facejup.skillpack.skills.skillshots.FireballRare;
import facejup.skillpack.skills.skillshots.Gravitize;
import facejup.skillpack.skills.skillshots.Ignite;
import facejup.skillpack.skills.skillshots.Magnetize;
import facejup.skillpack.skills.skillshots.Volley;
import facejup.skillpack.skills.targettedskills.Gift;
import facejup.skillpack.users.User;
import facejup.skillpack.users.UserManager;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;

public class Main extends JavaPlugin implements SkillPlugin,Listener {

	private HashMap<Player, List<Pair<Skill,ItemStack>>> binds = new HashMap<>(); 

	private UserManager um;
	private CommandManager cm;
	private EventManager em;
	private NPCManager npcm;

	private UpdateTimer timer;

	public void onEnable()
	{
		timer = new UpdateTimer(this);
		this.em = new EventManager(this);
		this.um = new UserManager(this);
		this.cm = new CommandManager(this);
		this.npcm = new NPCManager(this);
		this.getServer().getPluginManager().registerEvents(this, this);
		timer.startTimer();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			User user = em.getMain().getUserManager().getUser(player);
			player.setLevel(user.getLevel());
			player.setExp(1.0f*user.getLevelProgress() / user.getLevelExp(user.getLevel()));
		}
		/*	for(int i = 1; i <= 100; i++)
		{
			calc+= 20*Math.pow(1.1, i);
			System.out.println((i+1) + ": "+ 20*Math.pow(1.1, i) + "");
		}
		for(int i = 101; i <= 200; i++)
		{
			calc+= Math.pow(1.15, i);
			System.out.println((i+1) + ": "+ Math.pow(1.15, i) + "");
		}
		System.out.println(calc + "");*/
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			public void run()
			{
				npcm.loadShops();
			}
		}, 10L);
		npcm.loadShops();
	}


	public Skill getBindedSkill(Player player, ItemStack item)
	{
		if(!binds.containsKey(player))
			return null;
		for(Pair<Skill, ItemStack> bind : binds.get(player))
		{
			if(bind.getRight().isSimilar(item))
				return bind.getKey();
		}
		return null;
	}

	public ItemStack getBindedItemStack(Player player, Skill skill)
	{
		if(!binds.containsKey(player))
			return null;
		for(Pair<Skill, ItemStack> bind : binds.get(player))
		{
			if(bind.getLeft().equals(skill))
				return bind.getRight();
		}
		return null;
	}

	public void bindSkill(Player player, Skill skill)
	{
		if(getBindedSkill(player, player.getInventory().getItemInMainHand()) != null)
		{
			unbindSkill(player, player.getInventory().getItemInMainHand());
		}
		if(getBindedItemStack(player, skill) != null)
		{
			unbindSkill(player, getBindedItemStack(player, skill));
		}
		List<Pair<Skill, ItemStack>> playerbinds = new ArrayList<>();
		if(binds.containsKey(player))
			playerbinds = binds.get(player);
		playerbinds.add(Pair.of(skill, player.getInventory().getItemInMainHand()));
		binds.put(player, playerbinds);
		player.sendMessage(Chat.translate(Lang.tag + skill.getName() + " has been bound to " + Chat.formatItemName(player.getInventory().getItemInMainHand())));
	}

	public void unbindSkill(Player player, ItemStack item)
	{
		if(item == null || item.getType() == Material.AIR)
			return;
		if(!binds.containsKey(player))
		{
			player.sendMessage(Lang.tag + Chat.translate("You don't have a skill bound to &5" + Chat.formatItemName(item)));
			return;
		}
		for(Pair<Skill, ItemStack> bind : binds.get(player))
		{
			if(bind.getRight().isSimilar(item))
			{
				List<Pair<Skill,ItemStack>> playerbinds = binds.get(player);
				playerbinds.remove(bind);
				binds.put(player, playerbinds);
				player.sendMessage(Chat.translate(Lang.tag + bind.getLeft().getName() + " has been unbound from " + Chat.formatItemName(item)));
				return;
			}
		}
		player.sendMessage(Lang.tag + Chat.translate("You don't have a skill bound to &5" + Chat.formatItemName(item)));
		return;
	}

	@Override
	public void registerClasses(SkillAPI api) {
	}

	@Override
	public void registerSkills(SkillAPI api) {
		api.addSkill(new Fireball("Fireball", "SkillShot", Material.FIREBALL, 5));
		api.addSkill(new FireballRare("FireballRare", "SkillShot", Material.FIREBALL, 20));
		api.addSkill(new Displacement("Displacement", "SkillShot", Material.ENDER_PEARL, 5));
		api.addSkill(new Ignite("Ignite", "SkillShot", Material.BLAZE_POWDER, 5));
		api.addSkill(new Drill("Drill", "SkillShot", Material.DIAMOND_PICKAXE, 5));
		api.addSkill(new Volley("Volley", "SkillShot", Material.ARROW, 10));
		api.addSkill(new Eruption("Eruption", "SkillShot", Material.TNT, 4));
		api.addSkill(new Static("Static", "PassiveSkill", Material.LAPIS_BLOCK, 3));
		api.addSkill(new Magnetize("Magnetize", "SkillShot", Material.GOLD_INGOT, 10));
		api.addSkill(new Gravitize("Gravitize", "SkillShot", Material.BRICK, 5));
		api.addSkill(new FireLeap("FireLeap", "SkillShot", Material.FEATHER, 5));
		api.addSkill(new Gift("Gift", "TargetSkill", Material.EMERALD, 1));
	}

	@EventHandler
	public void projectileHit(ProjectileHitEvent event)
	{
		if(event.getHitEntity() != null && event.getEntity() instanceof Arrow)
		{
			event.getEntity().setBounce(false);
			event.getEntity().remove();
		}
	}

	public NPCManager getNPCManager()
	{
		return this.npcm;
	}

	public CommandManager getCommandManager()
	{
		return this.cm;
	}

	public EventManager getEventManager()
	{
		return this.em;
	}

	public UserManager getUserManager()
	{
		return this.um;
	}

}
