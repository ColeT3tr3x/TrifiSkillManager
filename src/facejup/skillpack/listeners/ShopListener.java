package facejup.skillpack.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;

import facejup.skillpack.main.EventManager;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;
import facejup.skillpack.util.Numbers;
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
		if(!name.contains("Skill") || SkillAPI.getSkill(ChatColor.stripColor(name).substring(name.indexOf(" ")-1, name.lastIndexOf(" ")-4)) == null)
		{
			if(!item.getItemMeta().hasLore())
				return;
			List<String> lore = item.getItemMeta().getLore();
			String coststr = item.getItemMeta().getLore().get(item.getItemMeta().getLore().size()-1);
			if(!ChatColor.stripColor(coststr).startsWith("Cost: "))
				return;
			if(!Numbers.isInt(coststr.substring(coststr.indexOf(" ") +1, coststr.lastIndexOf(" "))))
				return;
			int cost = Integer.parseInt(coststr.substring(coststr.indexOf(" ") +1, coststr.lastIndexOf(" ")));
			if(em.getMain().getEconomy().getBalance(player) >= cost)
			{
				em.getMain().getEconomy().withdrawPlayer(player, cost);
				ItemStack newitem = item.clone();
				ItemMeta meta = newitem.getItemMeta();
				List<String> newlore = (meta.getLore().size() > 1?meta.getLore().subList(0, meta.getLore().size()-2):new ArrayList<>());
				meta.setLore(newlore);
				if(meta.hasDisplayName() && Numbers.isInt(ChatColor.stripColor(meta.getDisplayName().substring(0, meta.getDisplayName().indexOf(" ")))))
					meta.setDisplayName(ChatColor.RESET + meta.getDisplayName().substring(meta.getDisplayName().indexOf(" ")+1, meta.getDisplayName().length()));
				newitem.setItemMeta(meta);
				player.getInventory().addItem(newitem);
				player.sendMessage(Chat.translate(Lang.tag + "You purchased " + item.getItemMeta().getDisplayName() + " &afor &6" + cost + " &aCoins!"));
				return;
			}
			else
			{
				player.sendMessage(Chat.translate(Lang.tag + "Not enough currency to purchase: " + em.getMain().getEconomy().getBalance(player) + "/" + cost));
				return;
			}
		}
		Skill skill = SkillAPI.getSkill(ChatColor.stripColor(name).substring(name.indexOf(" ")-1, name.lastIndexOf(" ")-4));
		User user = em.getMain().getUserManager().getUser(player);
		user.purchaseSkill(skill, item.getAmount());
	}

}
