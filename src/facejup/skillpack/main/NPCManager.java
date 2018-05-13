package facejup.skillpack.main;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;

import facejup.skillpack.util.FileControl;
import facejup.skillpack.util.Numbers;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class NPCManager {

	public HashMap<NPC, Shop> shops = new HashMap<>();

	private Main main;
	private FileControl fc;

	public NPCManager(Main main)
	{
		this.main = main;
		this.fc = new FileControl(new File(main.getDataFolder(), "shops.yml"));
	}
	
	public void loadShops()
	{
		if(fc.getConfig().contains("Shops") && fc.getConfig().getConfigurationSection("Shops").getKeys(false).size() > 0)
		{
			for(String str : fc.getConfig().getConfigurationSection("Shops").getKeys(false))
			{
				if(CitizensAPI.getNPCRegistry().getById((Numbers.isInt(str)?Integer.parseInt(str):-1)) != null)
				{
					NPC npc = CitizensAPI.getNPCRegistry().getById((Numbers.isInt(str)?Integer.parseInt(str):-1));
					shops.put(npc, new Shop(this, npc));
				}
			}
		}
	}
	
	public Main getMain()
	{
		return this.main;
	}

	public void createShop(NPC npc)
	{
		if(shops.containsKey(npc))
			return;
		if(fc.getConfig().contains("Shops." + npc.getId()))
			return;
		shops.put(npc, new Shop(this,npc));
	}

	public FileControl getFileControl()
	{
		return this.fc;
	}

}
