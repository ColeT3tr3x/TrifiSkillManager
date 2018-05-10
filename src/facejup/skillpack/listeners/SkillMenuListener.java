package facejup.skillpack.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.sucy.skill.SkillAPI;

import facejup.skillpack.main.EventManager;
import net.md_5.bungee.api.ChatColor;

public class SkillMenuListener implements Listener {
	
	private EventManager em;
	
	public List<Player> menuPlayers = new ArrayList<>();
	
	public SkillMenuListener(EventManager em)
	{
		this.em = em;
		em.getMain().getServer().getPluginManager().registerEvents(this, em.getMain());
	}
	
	@EventHandler
	public void invClose(InventoryCloseEvent event)
	{
		if(menuPlayers.contains(event.getPlayer()))
			menuPlayers.remove(event.getPlayer());
	}

	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent event)
	{
		if(!(event.getWhoClicked() instanceof Player))
			return;
		Player player = (Player) event.getWhoClicked();
		if(!menuPlayers.contains(player))
			return;
		event.setCancelled(true);
		if(event.getClickedInventory() == null)
			return;
		if(event.getCurrentItem() == null)
			return;
		if(!event.getCurrentItem().hasItemMeta())
			return;
		if(!event.getCurrentItem().getItemMeta().hasDisplayName())
			return;
		if(event.getClickedInventory().getType() == InventoryType.PLAYER)
			return;
		ItemStack item = event.getCurrentItem();
		String name = item.getItemMeta().getDisplayName();
		if(SkillAPI.getSkill(ChatColor.stripColor(name).substring(0, name.indexOf(" ")-2)) == null)
			return;
		em.getMain().getUserManager().getUser(player).attemptIncSkillLevel(SkillAPI.getSkill(ChatColor.stripColor(name).substring(0, name.indexOf(" ")-2)));
		player.openInventory(em.getMain().getCommandManager().getSkillsCommand().getSkillsInventory(player));
		menuPlayers.add(player);
	}
	
}
