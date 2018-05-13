package facejup.skillpack.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sucy.skill.SkillAPI;

import facejup.skillpack.main.EventManager;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.InventoryBuilder;
import facejup.skillpack.util.ItemCreator;
import facejup.skillpack.util.Lang;
import facejup.skillpack.util.Numbers;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;

public class IdentifyListener implements Listener {

	private EventManager em;

	private List<Player> identifying = new ArrayList<>();

	public IdentifyListener(EventManager em)
	{
		this.em=em;
		this.em.getMain().getServer().getPluginManager().registerEvents(this, em.getMain());
	}

	@EventHandler
	public void playerOpenInventory(PlayerInteractAtEntityEvent event)
	{
		if(!CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked()))
			return;
		NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
		if(ChatColor.stripColor(npc.getName()).contains("Identifier"))
		{
			event.getPlayer().openInventory(new InventoryBuilder(event.getPlayer(), "Identifier", 1).setItem(8, new ItemCreator(Material.EMERALD).setDisplayname("&6Confirm Identification").setLore(Arrays.asList(ChatColor.GOLD + "Cost: 0 Coins")).getItem()).getInventory());
			identifying.add(event.getPlayer());
		}
	}

	@EventHandler
	public void playerCloseInventory(InventoryCloseEvent event)
	{
		if(identifying.contains(event.getPlayer()))
		{
			identifying.remove(event.getPlayer());
			for(int i = 0; i < 8; i++)
			{
				if(event.getPlayer().getInventory().firstEmpty() > -1 && event.getInventory().getItem(i) != null)
					event.getPlayer().getInventory().addItem(event.getInventory().getItem(i));
				else
					event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), event.getInventory().getItem(i));
			}
		}
	}

	public void updateInventory(Inventory inv)
	{
		double cost = getWholeCost(inv);
		ItemStack item = inv.getItem(8);
		List<String> coststr = Arrays.asList(ChatColor.GOLD + "Cost: " + cost);
		item = new ItemCreator(item.clone()).setLore(coststr).getItem();
		inv.setItem(8, item);
	}
	
	public double getCost(ItemStack item)
	{
		if(item == null)
			return 0;
		if(!item.getItemMeta().hasLore())
			return 0;
		String strlvl = item.getItemMeta().getLore().get(0);
		if(!Numbers.isInt(strlvl.substring(strlvl.indexOf(" ")+1)))
			return 0;
		int level = Integer.parseInt(strlvl.substring(strlvl.indexOf(" ")+1));
		return 10*Math.pow(1.75, level);
	}
	
	public double getWholeCost(Inventory inv)
	{
		double cost = 0;
		for(int i = 0; i < 8; i++)
		{
			ItemStack item = inv.getItem(i);
			cost+=getCost(item);
		}
		return cost;
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent event)
	{
		if(!identifying.contains(event.getWhoClicked()))
			return;
		Player player = (Player) event.getWhoClicked();
		if(event.getClickedInventory() == null)
			return;
		if(event.getCurrentItem() == null)
			return;
		ItemStack item = event.getCurrentItem();
		Inventory invOpen = (Inventory) player.getOpenInventory().getTopInventory();
		if(event.getClickedInventory().getType() == InventoryType.PLAYER && invOpen.firstEmpty() > -1)
		{
			if(item.getItemMeta().hasDisplayName() && ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("Unidentified") && item.getItemMeta().hasLore())
			{
				event.setCancelled(true);
				if(item.getAmount() > 1)
				{
					invOpen.setItem(invOpen.firstEmpty(), new ItemCreator(item.clone()).setAmount(1).getItem());
					item.setAmount(item.getAmount()-1);
				}
				else
				{
					invOpen.setItem(invOpen.firstEmpty(), item);
					player.getInventory().setItem(event.getSlot(), null);
				}
			}
			else
			{
				event.setCancelled(true);
				player.sendMessage(Chat.translate(Lang.tag + "Must click an unidentified weapon"));
			}
			updateInventory(invOpen);
			player.updateInventory();
		}
		if(event.getClickedInventory().getType() != InventoryType.PLAYER && event.getSlot() < 8 && player.getInventory().firstEmpty() > -1)
		{
			player.getInventory().addItem(item);
			event.setCancelled(true);
			invOpen.setItem(event.getSlot(), null);
			updateInventory(invOpen);
			player.updateInventory();
		}
		else if(event.getClickedInventory().getType() != InventoryType.PLAYER && event.getSlot() == 8 && player.getInventory().firstEmpty() > -1)
		{
			if(em.getMain().getEconomy().getBalance(player) >= getWholeCost(invOpen))
			{
				for(int i = 0; i < 8; i++)
				{
					if(invOpen.getItem(i) == null)
						continue;
					ItemStack invItem = invOpen.getItem(i);
					if(!invItem.getItemMeta().hasLore())
						continue;
					String strlvl = invItem.getItemMeta().getLore().get(0);
					if(!Numbers.isInt(strlvl.substring(strlvl.indexOf(" ")+1)))
						continue;
					int level = Integer.parseInt(strlvl.substring(strlvl.indexOf(" ")+1));
					ItemStack item2 = new ItemCreator(ItemCreator.getRandomWeapon()).addSkills(SkillAPI.getSkills().values().stream().filter(item4 -> Numbers.getRandom(0, 100/level)==1).limit(level).collect(Collectors.toList()), level).getItem();
					player.getInventory().addItem(item2);
				}
				event.setCancelled(true);
				invOpen.clear();
				if(player.getOpenInventory() != null)
				player.closeInventory();
				em.getMain().getEconomy().withdrawPlayer(player, getWholeCost(invOpen));
			}
		}
	}

	@EventHandler
	public void entityDeathEvent(EntityDeathEvent event)
	{
		if(event.getEntity() instanceof Monster)
		{
			event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), ItemCreator.getUnidentified(10));
		}
	}

}
