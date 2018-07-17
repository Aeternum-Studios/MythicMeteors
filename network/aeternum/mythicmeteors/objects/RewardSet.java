package network.aeternum.mythicmeteors.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardSet {
	private List< ItemStack > items;
	
	public RewardSet( List< ItemStack > items ) {
		this.items = items;
	}
	
	public List< ItemStack > getRewards( Player player ) {
		List< ItemStack > copy = new ArrayList< ItemStack >();
		for ( ItemStack item : items ) {
			copy.add( item.clone() );
		}
		return copy;
	}

	public List< ItemStack > getItems() {
		return items;
	}
}

