package io.github.bananapuncher714.mythicmeteors.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.mythicmeteors.MythicMeteors;
import io.github.bananapuncher714.mythicmeteors.util.SpawnUtil;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

public class MeteorEvent {
	String id;
	Set< ActiveMob > bosses = new HashSet< ActiveMob >();
	Location center;
	List< Location > chests;
	long start = System.currentTimeMillis();
	long msDuration;
	boolean done = false;

	public MeteorEvent( String id, MythicMob boss, int level, int amount, Location location, List< RewardSet > rewards, long duration ) {
		this.id = id;
		for ( int i = 0; i < amount; i++ ) {
			bosses.add( boss.spawn( BukkitAdapter.adapt( location ), level ) );
			System.out.println( location.getY() );
		}
		location.getWorld().spawnParticle( Particle.SMOKE_LARGE, location.getBlockX() + .5, location.getBlockY() + .5, location.getBlockZ() + .5, 60, 2, 2, 2, 0 );
		location.getWorld().playSound( location, Sound.ENTITY_GENERIC_EXPLODE, 15, 1 );
		center = location;
		msDuration = duration;

		chests = new ArrayList< Location >();
		for ( RewardSet reward : rewards ) {
			Location spawn = SpawnUtil.getRandomSpawn( center, 10, 2 );
			spawn.getBlock().setType( Material.CHEST );
			Chest state = ( Chest ) spawn.getBlock().getState();
			for ( ItemStack item : reward.getItems() ) {
				state.getInventory().addItem( item );
			}
			location.getWorld().spawnParticle( Particle.EXPLOSION_HUGE, spawn.getBlockX() + .5, spawn.getBlockY() + .5, spawn.getBlockZ() + .5, 10, 1, 1, 1, 0 );
			location.getWorld().playSound( location, Sound.BLOCK_ANVIL_LAND, 15, 1 );
			chests.add( spawn );
		}

		for ( Player player : location.getWorld().getPlayers() ) {
			player.sendMessage( MythicMeteors.thWord( "notifications.started-event", player ) );
			player.playSound( player.getLocation(), Sound.AMBIENT_CAVE, 1, 1 );
		}
	}

	public String getId() {
		return id;
	}

	public boolean update() {
		if ( done ) {
			return false;
		}
		if ( System.currentTimeMillis() - start > msDuration ) {
			stop( false );
			return false;
		}
		for ( Location location : chests ) {
			if ( location.getBlock().getType() == Material.CHEST ) {
				location.getWorld().spawnParticle( Particle.FLAME, location.getBlockX() + .5, location.getBlockY() + .5, location.getBlockZ() + .5, 10, .7, .7, .7, 0 );
			}
		}
		return true;
	}

	public void stop( boolean force ) {
		boolean isBossesDead = true;
		for ( ActiveMob mob : bosses ) {
			if ( !mob.getLivingEntity().isDead() ) {
				isBossesDead = false;
			}
		}
		if ( !force ) {
			for ( Player player : center.getWorld().getPlayers() ) {
				if ( isBossesDead ) {
					player.sendMessage( MythicMeteors.thWord( "notifications.completed-event", player ) );
					player.playSound( player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1 );
				} else {
					player.sendMessage( MythicMeteors.thWord( "notifications.failed-event", player ) );
					player.playSound( player.getLocation(), Sound.ENTITY_ENDERMEN_DEATH, 1, 1 );
				}
			}
		}

		for ( Location location : chests ) {
			Block block = location.getBlock();
			if ( block.getType() == Material.CHEST ) {
				Chest chest = ( Chest ) block.getState();
				if ( force || !isBossesDead ) {
					chest.getInventory().clear();
					chest.setType( Material.AIR );
					chest.update();
				} else {
					location.getWorld().playSound( location, Sound.ENTITY_CHICKEN_EGG, 15, 1 );
				}
				block.setType( Material.AIR );
			}
		}
		for ( ActiveMob mob : bosses ) {
			mob.getLivingEntity().remove();
		}
	}

	public boolean onMythicMobDeath( ActiveMob mob ) {
		if ( bosses.contains( mob ) ) {
			bosses.remove( mob );
			if ( bosses.size() == 0 ) {
				Bukkit.getScheduler().scheduleSyncDelayedTask( MythicMeteors.getPlugin( MythicMeteors.class ), new Runnable() {
					@Override
					public void run() {
						done = true;
						stop( false );
					}
				} );
			}
			return true;
		}
		return false;
	}
}
