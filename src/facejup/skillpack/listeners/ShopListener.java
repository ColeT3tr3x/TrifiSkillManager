package facejup.skillpack.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.main.EventManager;
import facejup.skillpack.users.User;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;

public class ShopListener implements Listener {
	
	private EventManager em;
	
	private List<Player> shopping = new ArrayList<>();
	
	public ShopListener(EventManager em)
	{
		this.em = em;
		em.getMain().getServer().getPluginManager().registerEvents(this, em.getMain());
	}
	
	@EventHandler
	public void playerOpen(PlayerInteractEntityEvent event)
	{
		if(CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked()) == null)
			return;
		NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
		if(em.getMain().getNPCManager().shops.containsKey(npc))
		{
			shopping.add(event.getPlayer());
			event.getPlayer().openInventory(em.getMain().getNPCManager().shops.get(npc).getShopInventory());
		}
	}
	
	@EventHandler
	public void invClose(InventoryCloseEvent event)
	{
		if(shopping.contains(event.getPlayer()))
			shopping.remove(event.getPlayer());
	}
	
	@EventHandler
	public void inventoryClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		if(!shopping.contains(player))
			return;
		if(event.getClickedInventory() == null)
			return;
		if(event.getClickedInventory().getType() == InventoryType.PLAYER)
			return;
		event.setCancelled(true);
		if(event.getCurrentItem() == null)
			return;
		if(!event.getCurrentItem().hasItemMeta())
			return;
		if(!event.getCurrentItem().getItemMeta().hasDisplayName())
			return;
		ItemStack item = event.getCurrentItem();
		String name = item.getItemMeta().getDisplayName();
		if(SkillAPI.getSkill(ChatColor.stripColor(name).substring(0, name.indexOf(" ")-2)) == null)
			return;
		Skill skill = SkillAPI.getSkill(ChatColor.stripColor(name).substring(0, name.indexOf(" ")-2));
		User user = em.getMain().getUserManager().getUser(player);
		user.purchaseSkill(skill, item.getAmount());
	}

}
