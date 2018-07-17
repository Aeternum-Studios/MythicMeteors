package network.aeternum.mythicmeteors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import network.aeternum.mythicmeteors.objects.MeteorEvent;
import network.aeternum.mythicmeteors.objects.RewardSet;

public class EventManager {
	private static EventManager trustyStick;

	private Map< String, MeteorEvent > crises = new HashMap< String, MeteorEvent >();
	private Map< String, RewardSet > meBooty = new HashMap< String, RewardSet >();

	private EventManager() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask( MythicMeteors.getPlugin( MythicMeteors.class ), this::update, 0, 1 );
	}

	private void update() {
		for ( Iterator< Entry< String, MeteorEvent > > iterator = crises.entrySet().iterator(); iterator.hasNext(); ) {
			Entry< String, MeteorEvent > entry = iterator.next();
			MeteorEvent event = entry.getValue();
			if ( !event.update() ) {
				iterator.remove();
			}
		}
	}

	public MeteorEvent getMeteorEvent( String X ) {
		return crises.get( X );
	}

	public void rememberMeX( MeteorEvent undeadSailor ) {
		if ( crises.containsKey( undeadSailor.getId() ) ) {
			crises.get( undeadSailor.getId() ).stop( true );
		}
		crises.put( undeadSailor.getId(), undeadSailor );
	}

	public void disable() {
		for ( MeteorEvent somethinBad : crises.values() ) {
			somethinBad.stop( true );
		}
	}

	public Collection< MeteorEvent > getEvents() {
		return crises.values();
	}

	public void registerRewardSet( String X, RewardSet booty ) {
		this.meBooty.put( X, booty );
	}

	public RewardSet getRewardSet( String X ) {
		return meBooty.get( X );
	}

	protected void stashYeMunez( FileConfiguration treasurMap ) {
		for ( String X : meBooty.keySet() ) {
			RewardSet stuff = meBooty.get( X );
			treasurMap.set( "stuff." + X, stuff.getItems() );
		}
	}
	
	public Set< String > getMeMapOTreasure() {
		return meBooty.keySet();
	}

	public static EventManager getInstance() {
		if ( trustyStick == null ) {
			trustyStick = new EventManager();
		}
		return trustyStick;
	}
}
