package facejup.skillpack.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;

import facejup.skillpack.main.EventManager;
import facejup.skillpack.users.User;
import facejup.skillpack.util.Chat;
import facejup.skillpack.util.Lang;
import facejup.skillpack.util.Numbers;
import net.md_5.bungee.api.ChatColor;

public class ScrollUseListener implements Listener {

	private EventManager em;

	public ScrollUseListener(EventManager em)
	{
		this.em = em;
		em.getMain().getServer().getPluginManager().registerEvents(this, em.getMain());
	}

	@EventHandler
	public void useScrollCast(PlayerInteractEvent event)
	{
		if(!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
			return;
		if(event.getItem() == null || event.getItem().getType() == Material.AIR)
			return;
		Player player = event.getPlayer();
		User user = em.getMain().getUserManager().getUser(player);
		ItemStack item = event.getItem();
		if(item.getType() != Material.PAPER)
			return;
		if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			return;
		ItemMeta meta = item.getItemMeta();
		String name = meta.getDisplayName();
		if(SkillAPI.getSkill(name.substring(name.indexOf(" ", 12)+1,name.lastIndexOf(" "))) == null)
			return;
		Skill skill = SkillAPI.getSkill(name.substring(name.indexOf(" ", 12)+1,name.lastIndexOf(" ")));
		String level = name.substring(name.lastIndexOf("(")+1, name.lastIndexOf("/"));
		if(!Numbers.isInt(level))
			return;
		int uses = -1;
		if(item.getItemMeta().hasLore())
		{
			String loreline = item.getItemMeta().getLore().get(item.getItemMeta().getLore().size()-1);
			if(ChatColor.stripColor(loreline).startsWith("Uses: "))
			{
				String struses = ChatColor.stripColor(loreline);
				if(Numbers.isInt(struses.substring(6,struses.length())))
					uses = Integer.parseInt(struses.substring(6,struses.length()))-1;
				if(uses >= 0)
				{
					List<String> lore = item.getItemMeta().getLore();
					lore.set(lore.size()-1, ChatColor.AQUA + "Uses: " + (uses));
					meta.setLore(lore);
					item.setItemMeta(meta);
				}
			}
		}
		if(ChatColor.stripColor(name).startsWith("Scroll: Cast "))
		{
			if(skill instanceof SkillShot)
			{
				user.incMana(20);
				((SkillShot) skill).cast(player, Integer.parseInt(level));
				if(uses == 0)
					item.setAmount(item.getAmount()-1);
				else if(uses > 0)
					Chat.sendActionBar(player, ChatColor.AQUA + "Uses: " + uses);
			}
		}
		else if(ChatColor.stripColor(name).startsWith("Scroll: Learn "))
		{
			if(user.hasSkill(skill, Integer.parseInt(level)))
			{
				player.sendMessage(Chat.translate(Lang.tag + "You already have that skill."));
			}
			else
			{
				user.unlockSkill(skill, Integer.parseInt(level));
				if(uses == 0)
					item.setAmount(item.getAmount()-1);
				else if(uses > 0)
					Chat.sendActionBar(player, ChatColor.AQUA + "Uses: " + uses);
			}
		}
	}

}
