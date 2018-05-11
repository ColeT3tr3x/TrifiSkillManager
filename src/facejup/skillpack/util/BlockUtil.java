package facejup.skillpack.util;

import org.bukkit.Location;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.inventory.InventoryHolder;

public class BlockUtil {
	public static void copyBlockToLocation(BlockState sourceState, Location location) {
		Block targetBlock = location.getBlock();
		//
		targetBlock.setType(sourceState.getType());
		BlockState targetState = targetBlock.getState();
		targetState.setType(sourceState.getType());
		targetState.setData(sourceState.getData());
		// TODO Can we not just copy the state over itself, without copying its individual members?
		if(sourceState instanceof InventoryHolder) {
			InventoryHolder sourceHolder = (InventoryHolder) sourceState;
			InventoryHolder targetHolder = (InventoryHolder) targetState;
			targetHolder.getInventory().setContents(sourceHolder.getInventory().getContents());
		}
		//
		if(sourceState instanceof Banner) {
			Banner sourceBanner = (Banner) sourceState;
			Banner targetBanner = (Banner) targetState;
			targetBanner.setBaseColor(sourceBanner.getBaseColor());
			targetBanner.setPatterns(sourceBanner.getPatterns());
		} else if(sourceState instanceof BrewingStand) {
			BrewingStand sourceStand = (BrewingStand) sourceState;
			BrewingStand targetStand = (BrewingStand) targetState;
			targetStand.setBrewingTime(sourceStand.getBrewingTime());
		} else if(sourceState instanceof Chest) {
			Chest sourceChest = (Chest) sourceState;
			Chest targetChest = (Chest) targetState;
			targetChest.getBlockInventory().setContents(sourceChest.getBlockInventory().getContents());
		} else if(sourceState instanceof CommandBlock) {
			CommandBlock sourceCommandBlock = (CommandBlock) sourceState;
			CommandBlock targetCommandBlock = (CommandBlock) targetState;
			targetCommandBlock.setName(sourceCommandBlock.getName());
			targetCommandBlock.setCommand(sourceCommandBlock.getCommand());
		} else if(sourceState instanceof CreatureSpawner) {
			CreatureSpawner sourceSpawner = (CreatureSpawner) sourceState;
			CreatureSpawner targetSpawner = (CreatureSpawner) targetState;
			targetSpawner.setSpawnedType(sourceSpawner.getSpawnedType());
			targetSpawner.setDelay(sourceSpawner.getDelay());
		} else if(sourceState instanceof Furnace) {
			Furnace sourceFurnace = (Furnace) sourceState;
			Furnace targetFurnace = (Furnace) targetState;
			targetFurnace.setBurnTime(sourceFurnace.getBurnTime());
			targetFurnace.setCookTime(sourceFurnace.getCookTime());
		} else if(sourceState instanceof Jukebox) {
			Jukebox sourceJukebox = (Jukebox) sourceState;
			Jukebox targetJukebox = (Jukebox) targetState;
			targetJukebox.setPlaying(sourceJukebox.getPlaying());
		} else if(sourceState instanceof NoteBlock) {
			NoteBlock sourceNoteBlock = (NoteBlock) sourceState;
			NoteBlock targetNoteBlock = (NoteBlock) targetState;
			targetNoteBlock.setNote(sourceNoteBlock.getNote());
		} else if(sourceState instanceof Sign) {
			Sign sourceSign = (Sign) sourceState;
			Sign targetSign = (Sign) targetState;
			String[] lines = sourceSign.getLines();
			for (int i = 0; i < lines.length; i++) {
				targetSign.setLine(i, lines[i]);
			}
		} else if(sourceState instanceof Skull) {
			Skull sourceSkull = (Skull) sourceState;
			Skull targetSkull = (Skull) targetState;
			targetSkull.setOwner(sourceSkull.getOwner());
			targetSkull.setRotation(sourceSkull.getRotation());
			targetSkull.setSkullType(sourceSkull.getSkullType());
		}
		//
		targetState.update();
	}

}
