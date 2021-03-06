package facejup.skillpack.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.SkillPlugin;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.skills.passives.BlastResistance;
import facejup.skillpack.skills.passives.FireResistance;
import facejup.skillpack.skills.passives.Gills;
import facejup.skillpack.skills.passives.Gorgon;
import facejup.skillpack.skills.passives.PoisonResistance;
import facejup.skillpack.skills.passives.Presence;
import facejup.skillpack.skills.passives.ProjectileResistance;
import facejup.skillpack.skills.passives.Static;
import facejup.skillpack.skills.passives.Thorns;
import facejup.skillpack.skills.passives.TwistedSight;
import facejup.skillpack.skills.passives.Weightless;
import facejup.skillpack.skills.passives.WitherResistance;
import facejup.skillpack.skills.skillshots.Displacement;
import facejup.skillpack.skills.skillshots.Drill;
import facejup.skillpack.skills.skillshots.Eruption;
import facejup.skillpack.skills.skillshots.FireLeap;
import facejup.skillpack.skills.skillshots.Fireball;
import facejup.skillpack.skills.skillshots.FireballRare;
import facejup.skillpack.skills.skillshots.Gravitize;
import facejup.skillpack.skills.skillshots.Ignite;
import facejup.skillpack.skills.skillshots.LightningBall;
import facejup.skillpack.skills.skillshots.Lullaby;
import facejup.skillpack.skills.skillshots.Magnetize;
import facejup.skillpack.skills.skillshots.MirrorImage;
import facejup.skillpack.skills.skillshots.Projection;
import facejup.skillpack.skills.skillshots.Pyromancy;
import facejup.skillpack.skills.skillshots.Telekinesis;
import facejup.skillpack.skills.skillshots.Volley;
import facejup.skillpack.skills.targettedskills.Gift;
import facejup.skillpack.skills.targettedskills.Leviosa;
import facejup.skillpack.skills.targettedskills.MindTrap;
import facejup.skillpack.skills.targettedskills.MindWall;
import facejup.skillpack.users.User;
import facejup.skillpack.users.UserManager;
import facejup.skillpack.util.Bind;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements SkillPlugin,Listener {

	public static Main instance;

	private HashMap<Player, List<Bind>> binds = new HashMap<>(); 

	private UserManager um;
	private CommandManager cm;
	private EventManager em;
	private NPCManager npcm;

	private Economy econ;

	private UpdateTimer timer;
	
	public void onEnable()
	{
		instance = this;
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
		if (!setupEconomy()) {
			this.getLogger().severe("Disabled due to no Vault dependency found!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
	}

	public Economy getEconomy()
	{
		return this.econ;
	}

	private boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public Skill getBindedSkill(Player player, ItemStack item)
	{
		if(!binds.containsKey(player))
			return null;
		for(Bind bind : binds.get(player))
		{
			if(bind.item.isSimilar(item))
				return bind.skill;
		}
		return null;
	}

	public ItemStack getBindedItemStack(Player player, Skill skill)
	{
		if(!binds.containsKey(player))
			return null;
		for(Bind bind : binds.get(player))
		{
			if(bind.skill.equals(skill))
				return bind.item;
		}
		return null;
	}

	public int getBindedSkillLevel(Player player, Skill skill)
	{
		if(!binds.containsKey(player))
			return 0;
		for(Bind bind : binds.get(player))
		{
			if(bind.skill.equals(skill))
				return bind.level;
		}
		return 0;
	}

	public void bindSkill(Player player, Skill skill, int level)
	{
		if(getBindedSkill(player, player.getInventory().getItemInMainHand()) != null)
		{
			unbindSkill(player, player.getInventory().getItemInMainHand());
		}
		if(getBindedItemStack(player, skill) != null)
		{
			unbindSkill(player, getBindedItemStack(player, skill));
		}
		List<Bind> playerbinds = new ArrayList<>();
		if(binds.containsKey(player))
			playerbinds = binds.get(player);
		playerbinds.add(new Bind(skill, player.getInventory().getItemInMainHand(), level));
		binds.put(player, playerbinds);
		player.sendMessage(Chat.translate(Lang.tag + skill.getName() + " " + level + " has been bound to " + Chat.formatItemName(player.getInventory().getItemInMainHand())));
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
		for(Bind bind : binds.get(player))
		{
			if(bind.item.isSimilar(item))
			{
				List<Bind> playerbinds = binds.get(player);
				playerbinds.remove(bind);
				binds.put(player, playerbinds);
				player.sendMessage(Chat.translate(Lang.tag + bind.skill.getName() + " has been unbound from " + Chat.formatItemName(item)));
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
		api.addSkill(new LightningBall("LightningBall", "SkillShot", Material.FIREBALL, 5));
		api.addSkill(new FireballRare("FireballRare", "SkillShot", Material.FIREBALL, 20));
		api.addSkill(new Displacement("Displacement", "SkillShot", Material.ENDER_PEARL, 5));
		api.addSkill(new Ignite("Ignite", "SkillShot", Material.BLAZE_POWDER, 5));
		api.addSkill(new Drill("Drill", "SkillShot", Material.DIAMOND_PICKAXE, 5));
		api.addSkill(new Volley("Volley", "SkillShot", Material.ARROW, 10));
		api.addSkill(new Eruption("Eruption", "SkillShot", Material.TNT, 4));
		api.addSkill(new MirrorImage("MirrorImage", "SkillShot", Material.SKULL, 4));
		api.addSkill(new Projection("Projection", "SkillShot", Material.SKULL, 2));
		api.addSkill(new Static("Static", "PassiveSkill", Material.LAPIS_BLOCK, 3));
		api.addSkill(new Pyromancy("Pyromancy", "SkillShot", Material.FIREBALL, 4));
		api.addSkill(new Telekinesis("Telekinesis", "SkillShot", Material.FEATHER, 4));
		api.addSkill(new BlastResistance("BlastResistance", "PassiveSkill", Material.TNT, 4));
		api.addSkill(new FireResistance("FireResistance", "PassiveSkill", Material.FLINT_AND_STEEL, 4));
		api.addSkill(new PoisonResistance("PoisonResistance", "PassiveSkill", Material.POTION, 4));
		api.addSkill(new WitherResistance("WitherResistance", "PassiveSkill", Material.DRAGONS_BREATH, 4));
		api.addSkill(new ProjectileResistance("ProjectileResistance", "PassiveSkill", Material.ARROW, 4));
		api.addSkill(new Thorns("Thorns", "PassiveSkill", Material.TRIPWIRE_HOOK, 4));
		api.addSkill(new Presence("Presence", "PassiveSkill", Material.EYE_OF_ENDER	, 4));
		api.addSkill(new Weightless("Weightless", "PassiveSkill", Material.FEATHER, 4));
		api.addSkill(new Gills("Gills", "PassiveSkill", Material.WATER_BUCKET, 4));
		api.addSkill(new TwistedSight("TwistedSight", "PassiveSkill", Material.EYE_OF_ENDER, 1));
		api.addSkill(new Gorgon("Gorgon", "PassiveSkill", Material.STONE, 4));
		api.addSkill(new Magnetize("Magnetize", "SkillShot", Material.GOLD_INGOT, 10));
		api.addSkill(new Lullaby("Lullaby", "SkillShot", Material.NOTE_BLOCK, 10));
		api.addSkill(new Gravitize("Gravitize", "SkillShot", Material.BRICK, 5));
		api.addSkill(new FireLeap("FireLeap", "SkillShot", Material.FEATHER, 5));
		api.addSkill(new Gift("Gift", "TargetSkill", Material.EMERALD, 1));
		api.addSkill(new Leviosa("Leviosa", "TargetSkill", Material.FEATHER, 1));
		api.addSkill(new MindWall("MindWall", "TargetSkill", Material.COBBLE_WALL, 1));
		api.addSkill(new MindTrap("MindTrap", "TargetSkill", Material.IRON_FENCE, 5));
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
