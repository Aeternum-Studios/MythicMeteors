package io.github.bananapuncher714.mythicmeteors.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.bananapuncher714.mythicmeteors.EventManager;
import io.github.bananapuncher714.mythicmeteors.objects.MeteorEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;

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
