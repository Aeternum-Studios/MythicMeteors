package network.aeternum.mythicmeteors.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import network.aeternum.mythicmeteors.EventManager;
import network.aeternum.mythicmeteors.objects.MeteorEvent;

public class MobListener implements Listener {
	EventManager manager = EventManager.getInstance();
	
	@EventHandler
	public void onMythicMobDeath( MythicMobDeathEvent event ) {
		for ( MeteorEvent meteor : manager.getEvents() ) {
			if ( meteor.onMythicMobDeath( event.getMob() ) ) {
				return;
			}
		}
	}
}
