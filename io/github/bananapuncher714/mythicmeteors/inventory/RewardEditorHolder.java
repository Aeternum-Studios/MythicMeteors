package io.github.bananapuncher714.mythicmeteors.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.mythicmeteors.ngui.inventory.BananaHolder;
import io.github.bananapuncher714.mythicmeteors.objects.RewardSet;
import net.md_5.bungee.api.ChatColor;

public class RewardEditorHolder extends BananaHolder {
	RewardSet set;
	Inventory inventory;
	
	public RewardEditorHolder( String id, RewardSet set ) {
		this.set = set;
		inventory = Bukkit.createInventory( this, 27, "Editing reward set " + id );
	}

	@Override
	public Inventory getInventory() {
		inventory.clear();
		for ( int i = 0; i < inventory.getSize(); i++ ) {
			if ( i >= set.getItems().size() ) {
				break;
			}
			inventory.setItem( i, set.getItems().get( i ) );
		}
		return inventory;
	}
	
	@Override
	public void onInventoryClose( InventoryCloseEvent event ) {
		set.getItems().clear();
		for ( ItemStack item : inventory.getContents() ) {
			if ( item != null && item.getType() != Material.AIR ) {
				set.getItems().add( item );
			}
		}
		event.getPlayer().sendMessage( ChatColor.GREEN + "Saved rewards!" );
	}

	@Override
	public void onInventoryClick( InventoryClickEvent event ) {
	}
}
