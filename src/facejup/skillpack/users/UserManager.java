package facejup.skillpack.users;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import facejup.skillpack.main.Main;
import facejup.skillpack.util.FileControl;

public class UserManager {
	
	private Main main;

	private FileControl fc; // FC containing the Users.yml file

	private HashMap<OfflinePlayer, User> users = new HashMap<>(); // Map storing the player and their user.
	
	public UserManager(Main main)
	{
		//TODO: Constructor which stores the given variables and loads the users.
		this.main = main;
		this.fc = new FileControl(new File(main.getDataFolder(), "users.yml"));
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
		{
			public void run()
			{
				for(Player player : Bukkit.getOnlinePlayers())
				{
					addUser(player);
				}
			}
		}, 10L);
	}
	public User getUser(OfflinePlayer player)
	{
		addUser(player);
		return users.get(player);
	}

	public void addUser(OfflinePlayer player) {
		if(!users.containsKey(player))
		{
			users.put(player, new User(this, player));
		}
	}
	
	public List<User> getUsers()
	{
		return this.users.values().stream().collect(Collectors.toList());
	}

	public FileControl getFileControl() {
		return this.fc;
	}

}
