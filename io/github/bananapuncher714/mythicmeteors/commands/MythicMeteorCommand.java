package io.github.bananapuncher714.mythicmeteors.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bananapuncher714.mythicmeteors.EventManager;
import io.github.bananapuncher714.mythicmeteors.MeteorPerms;
import io.github.bananapuncher714.mythicmeteors.MythicMeteors;
import io.github.bananapuncher714.mythicmeteors.inventory.RewardEditorHolder;
import io.github.bananapuncher714.mythicmeteors.objects.MeteorEvent;
import io.github.bananapuncher714.mythicmeteors.objects.RewardSet;
import io.github.bananapuncher714.mythicmeteors.util.OptionParser;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

public class MythicMeteorCommand implements CommandExecutor {
	private final Map< String, String > parameters = new HashMap< String, String >();

	public MythicMeteorCommand() {
		parameters.put( "-l", "LOCATION" );
		parameters.put( "-location", "LOCATION" );
		parameters.put( "-t", "TIME" );
		parameters.put( "-time", "TIME" );
		parameters.put( "-level", "LEVEL" );
		parameters.put( "-a", "AMOUNT" );
		parameters.put( "-amount", "AMOUNT" );
	}

	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( args.length > 0 ) {
			if ( args[ 0 ].equalsIgnoreCase( "create" ) ) {
				mutiny( sender, args );
			} else if ( args[ 0 ].equalsIgnoreCase( "stop" ) ) {
				tharBeGone( sender, args );
			} else if ( args[ 0 ].equalsIgnoreCase( "end" ) ) {
				sendinYeToDavyJones( sender, args );
			} else if ( args[ 0 ].equalsIgnoreCase( "edit" ) ) {
				wareBeMeKeys( sender, args );
			} else if ( args[ 0 ].equalsIgnoreCase( "help" ) ) {
				soYeBeNeedinHelp( sender );
			} else if ( args[ 0 ].equalsIgnoreCase( "list" ) ) {
				whereBeDavyJones( sender, args );
			} else if ( args[ 0 ].equalsIgnoreCase( "config" ) ) {
				config( sender, args );
			} else {
				soYeBeNeedinHelp( sender );
			}
		} else {
			soYeBeNeedinHelp( sender );
		}
		return false;
	}

	private void soYeBeNeedinHelp( CommandSender sender ) {
		if ( !MeteorPerms.beYeFirsMate( sender ) ) {
			sender.sendMessage( MythicMeteors.thWord( "command.no-permission", sender ) );
			return;
		}
		sender.sendMessage( ChatColor.GREEN + "=== Mythic Meteors ===" );
		sender.sendMessage( ChatColor.AQUA + "/mythicmeteor create <id> <mythicmob> <rewards> ... " + ChatColor.YELLOW + "- Starts the given event" );
		sender.sendMessage( ChatColor.AQUA + "/mythicmeteor stop <id> " + ChatColor.YELLOW + "- Stop the given event naturally" );
		sender.sendMessage( ChatColor.AQUA + "/mythicmeteor end <id> " + ChatColor.YELLOW + "- Stops the given event forcefully" );
		sender.sendMessage( ChatColor.AQUA + "/mythicmeteor list " + ChatColor.YELLOW + "- Lists the currently running events" );
		sender.sendMessage( ChatColor.AQUA + "/mythicmeteor help " + ChatColor.YELLOW + "- Shows this message" );
		if ( !MeteorPerms.beYeCapn( sender ) ) {
			return;
		}
		sender.sendMessage( ChatColor.AQUA + "/mythicmeteor edit <reward>" + ChatColor.YELLOW + "- Edit rewards" );
		sender.sendMessage( ChatColor.AQUA + "/mythicmeteor config <set|add|remove|get> <property> [value]" + ChatColor.YELLOW + "- Edit the config from afar" );
	}

	private void mutiny( CommandSender sender, String[] args ) {
		if ( !MeteorPerms.beYeFirsMate( sender ) ) {
			sender.sendMessage( MythicMeteors.thWord( "command.no-permission", sender ) );
			return;
		}

		if ( args.length < 4 ) {
			soYeBeNeedinHelp( sender );
			return;
		}

		String id = args[ 1 ];
		MythicMob boss = MythicMobs.getPlugin( MythicMobs.class ).getMobManager().getMythicMob( args[ 2 ] );
		if ( boss == null ) {
			sender.sendMessage( ChatColor.RED + args[ 2 ] + " is not a valid mob!" );
			return;
		}
		List< RewardSet > rewards = new ArrayList< RewardSet >();
		for ( String str : args[ 3 ].split( ";" ) ) {
			RewardSet set = EventManager.getInstance().getRewardSet( str );
			if ( set != null ) {
				rewards.add( set );
			} else {
				sender.sendMessage( ChatColor.RED + str + " is not a valid reward!" );
			}
		}
		if ( rewards.isEmpty() ) {
			sender.sendMessage( ChatColor.RED + "There are no valid rewards!" );
			return;
		}

		Map< String, String > arguments = new HashMap< String, String >();

		arguments.put( "TIME", "300000" );
		arguments.put( "AMOUNT", "1" );
		arguments.put( "LEVEL", "1" );

		OptionParser.parseArguments( parameters, arguments, 4, false, args );

		Location location;
		if ( !arguments.containsKey( "LOCATION" ) ) {
			if ( sender instanceof Player ) {
				Player player = ( Player ) sender;
				location = player.getLocation();
			} else {
				sender.sendMessage( ChatColor.RED + "A location is required!" );
				return;
			}
		} else {
			try {
				String[] loc = arguments.get( "LOCATION" ).split( ":" );
				location = new Location( Bukkit.getWorld( loc[ 0 ] ), Double.parseDouble( loc[ 1 ] ), 0, Double.parseDouble( loc[ 2 ] ) );
			} catch ( Exception exception ) {
				sender.sendMessage( ChatColor.RED + "Invalid location! Must be <world>:<x>:<z>" );
				return;
			}
		}
		long time;
		try {
			time = Long.parseLong( arguments.get( "TIME" ) );
		} catch ( Exception exception ) {
			sender.sendMessage( ChatColor.RED + "Invalid time! Must be a number!" );
			return;
		}

		int level;
		try {
			level = Integer.parseInt( arguments.get( "LEVEL" ) );
		} catch ( Exception exception ) {
			sender.sendMessage( ChatColor.RED + "Invalid time! Must be a number!" );
			return;
		}
		int amount;
		try {
			amount = Integer.parseInt( arguments.get( "AMOUNT" ) );
		} catch ( Exception exception ) {
			sender.sendMessage( ChatColor.RED + "Invalid amount! Must be a number!" );
			return;
		}

		sender.sendMessage( MythicMeteors.thWord( "command.starting-event", sender, args[ 1 ] ) );
		new BukkitRunnable() {
			Location spawn = location.clone();
			boolean init = false;

			@Override
			public void run() {
				if ( !init ) {
					init();
					init = true;
				}
				if ( spawn.getBlock().getType() != Material.AIR ) {
					MeteorEvent event = new MeteorEvent( id, boss, level, amount, location, rewards, time );
					EventManager.getInstance().rememberMeX( event );
					cancel();
				} else {
					spawn.getWorld().spawnParticle( Particle.SMOKE_LARGE, spawn.getX(), spawn.getY(), spawn.getZ(), 30, .3, 2, .3, 0 );
					spawn.getWorld().spawnParticle( Particle.FLAME, spawn.getX(), spawn.getY(), spawn.getZ(), 30, 0, 1, 0, .2 );
					spawn.subtract( 0, 1, 0 );
				}
			}

			private void init() {
				spawn.setY( spawn.getWorld().getMaxHeight() );
			}
		}.runTaskTimer( MythicMeteors.getPlugin( MythicMeteors.class ), 0, 1 );
		sender.sendMessage( MythicMeteors.thWord( "command.started-event", sender, args[ 1 ] ) );
	}

	private void tharBeGone( CommandSender sender, String[] args ) {
		if ( !MeteorPerms.beYeFirsMate( sender ) ) {
			sender.sendMessage( MythicMeteors.thWord( "command.no-permission", sender ) );
			return;
		}
		if ( args.length < 2 ) {
			sender.sendMessage( MythicMeteors.thWord( "command.event-required", sender ) );
			return;
		}
		MeteorEvent event = EventManager.getInstance().getMeteorEvent( args[ 1 ] );
		if ( event == null ) {
			sender.sendMessage( MythicMeteors.thWord( "command.invalid-event", sender ) );
			return;
		}
		event.stop( false );
		sender.sendMessage( MythicMeteors.thWord( "command.stopped-event-naturally", sender, args[ 1 ] ) );
	}

	private void sendinYeToDavyJones( CommandSender sender, String[] args ) {
		if ( !MeteorPerms.beYeFirsMate( sender ) ) {
			sender.sendMessage( MythicMeteors.thWord( "command.no-permission", sender ) );
			return;
		}
		if ( args.length < 2 ) {
			sender.sendMessage( MythicMeteors.thWord( "command.event-required", sender ) );
			return;
		}
		MeteorEvent event = EventManager.getInstance().getMeteorEvent( args[ 1 ] );
		if ( event == null ) {
			sender.sendMessage( MythicMeteors.thWord( "command.invalid-event", sender ) );
			return;
		}
		event.stop( true );
		sender.sendMessage( MythicMeteors.thWord( "command.stopped-event-forcefully", sender, args[ 1 ] ) );
	}

	private void whereBeDavyJones( CommandSender sender, String[] args ) {
		if ( !MeteorPerms.beYeFirsMate( sender ) ) {
			sender.sendMessage( MythicMeteors.thWord( "command.no-permission", sender ) );
			return;
		}
		sender.sendMessage( ChatColor.GREEN + "Events currently running:" );
		for ( MeteorEvent event : EventManager.getInstance().getEvents() ) {
			sender.sendMessage( "- " + event.getId() );
		}
	}

	private void wareBeMeKeys( CommandSender sender, String[] args ) {
		if ( !MeteorPerms.beYeCapn( sender ) ) {
			sender.sendMessage( MythicMeteors.thWord( "command.no-permission", sender ) );
			return;
		}
		if ( !( sender instanceof Player ) ) {
			sender.sendMessage( MythicMeteors.thWord( "command.must-be-player", sender ) );
			return;
		}
		if ( args.length < 2 ) {
			sender.sendMessage( MythicMeteors.thWord( "command.arguments-required", sender ) );
			return;
		}
		RewardSet set = EventManager.getInstance().getRewardSet( args[ 1 ] );
		if ( set == null ) {
			set = new RewardSet( new ArrayList< ItemStack >() );
			EventManager.getInstance().registerRewardSet( args[ 1 ], set );
		}
		Player player = ( Player ) sender;
		player.openInventory( new RewardEditorHolder( args[ 1 ], set ).getInventory() );
	}

	private void config( CommandSender sender, String[] args ) {
		if ( !MeteorPerms.beYeCapn( sender ) ) {
			sender.sendMessage( MythicMeteors.thWord( "command.no-permission", sender ) );
			return;
		}
		if ( args.length < 3 ) {
			sender.sendMessage( MythicMeteors.thWord( "command.config-usage", sender ) );
			sender.sendMessage( "&1[&9MythicMeteor&1] &cValid properties are mintime, maxtime, locations, bosses, and rewards ".replace( '&', '\u00a7' ) );
			return;
		}
		if ( args[ 1 ].equalsIgnoreCase( "get" ) ) {
			if ( args[ 2 ].equalsIgnoreCase( "mintime" ) ) {
				sender.sendMessage( "&1[&9MythicMeteor&1] &rMiniumum spawn time: ".replace( '&', '\u00a7' ) + MythicMeteors.getPlugin( MythicMeteors.class ).getMinTime() + " minutes" );
			} else if ( args[ 2 ].equalsIgnoreCase( "maxtime" ) ) {
				sender.sendMessage( "&1[&9MythicMeteor&1] &rMiniumum spawn time: ".replace( '&', '\u00a7' ) + MythicMeteors.getPlugin( MythicMeteors.class ).getMaxTime() + " minutes" );
			} else if ( args[ 2 ].equalsIgnoreCase( "bosses" ) ) {
				sender.sendMessage( "&1[&9MythicMeteor&1] &rBosses:".replace( '&', '\u00a7' ) );
				for ( String string : MythicMeteors.getPlugin( MythicMeteors.class ).getBosses() ) {
					sender.sendMessage( "&1[&9MythicMeteor&1] &r- ".replace( '&', '\u00a7' ) + string );
				}
			} else if ( args[ 2 ].equalsIgnoreCase( "locations" ) ) {
				sender.sendMessage( "&1[&9MythicMeteor&1] &rLocations:".replace( '&', '\u00a7' ) );
				for ( Location loc : MythicMeteors.getPlugin( MythicMeteors.class ).getLocations() ) {
					sender.sendMessage( "&1[&9MythicMeteor&1] &r- ".replace( '&', '\u00a7' ) + loc.getWorld().getName() + " " + loc.getBlockX() + ", " + loc.getBlockZ() );
				}
			} else if ( args[ 2 ].equalsIgnoreCase( "rewards" ) ) {
				sender.sendMessage( "&1[&9MythicMeteor&1] &rRewards:".replace( '&', '\u00a7' ) );
				for ( String string : MythicMeteors.getPlugin( MythicMeteors.class ).getRewards() ) {
					sender.sendMessage( "&1[&9MythicMeteor&1] &r- ".replace( '&', '\u00a7' ) + string );
				}
			} else {
				sender.sendMessage( "&1[&9MythicMeteor&1] &cInvalid property! Valid ones are mintime, maxtime, locations, bosses, and rewards ".replace( '&', '\u00a7' ) );
			}

		} else if ( args[ 1 ].equalsIgnoreCase( "set" ) || args[ 1 ].equalsIgnoreCase( "add" ) || args[ 1 ].equalsIgnoreCase( "remove" ) ) {
			if ( args.length < 4 ) {
				sender.sendMessage( MythicMeteors.thWord( "command.config-usage", sender ) );
				sender.sendMessage( "&1[&9MythicMeteor&1] &cValid properties are locations, bosses, and rewards ".replace( '&', '\u00a7' ) );
				return;
			}
			if ( args[ 1 ].equalsIgnoreCase( "set" ) ) {
				if ( args[ 2 ].equalsIgnoreCase( "mintime" ) ) {
					try {
						MythicMeteors.getPlugin( MythicMeteors.class ).setMinTime( Integer.parseInt( args[ 3 ] ) );
						sender.sendMessage( "&1[&9MythicMeteor&1] &rSet miniumum spawn time!".replace( '&', '\u00a7' ) );
					} catch ( Exception exception ) {
						sender.sendMessage(  "&1[&9MythicMeteor&1]".replace( '&', '\u00a7' ) + ChatColor.RED + " Time provided must be a number in minutes!" );
					}
				} else if ( args[ 2 ].equalsIgnoreCase( "maxtime" ) ) {
					try {
						MythicMeteors.getPlugin( MythicMeteors.class ).setMaxTime( Integer.parseInt( args[ 3 ] ) );
						sender.sendMessage( "&1[&9MythicMeteor&1] &rSet maximum spawn time!".replace( '&', '\u00a7' ) );
					} catch ( Exception exception ) {
						sender.sendMessage(  "&1[&9MythicMeteor&1]".replace( '&', '\u00a7' ) + ChatColor.RED + " Time provided must be a number in minutes!" );
					}
				} else {
					sender.sendMessage( "&1[&9MythicMeteor&1] &cInvalid property! Valid ones are mintime, maxtime! Perhaps you want 'get/remove'?".replace( '&', '\u00a7' ) );
				}
			} else {
				if ( args[ 2 ].equalsIgnoreCase( "bosses" ) ) {
					MythicMob mob = MythicMobs.getPlugin( MythicMobs.class ).getMobManager().getMythicMob( args[ 3 ] );
					if ( mob == null ) {
						sender.sendMessage( "&1[&9MythicMeteor&1] &cInvalid MythicMob!".replace( '&', '\u00a7' ) );
					} else {
						if ( args[ 1 ].equalsIgnoreCase( "add" ) ) {
							MythicMeteors.getPlugin( MythicMeteors.class ).getBosses().add( args[ 3 ] );
							sender.sendMessage( "&1[&9MythicMeteor&1] &aAdded MythicMob successfully!".replace( '&', '\u00a7' ) );
						} else {
							MythicMeteors.getPlugin( MythicMeteors.class ).getBosses().remove( args[ 3 ] );
							sender.sendMessage( "&1[&9MythicMeteor&1] &aRemoved MythicMob successfully!".replace( '&', '\u00a7' ) );
						}
					}
				} else if ( args[ 2 ].equalsIgnoreCase( "locations" ) ) {
					try {
						String[] parts = args[ 3 ].split( ";" );
						World world = Bukkit.getWorld( parts[ 0 ] );
						int x = Integer.parseInt( parts[ 1 ] );
						int z = Integer.parseInt( parts[ 2 ] );

						Location location = new Location( world, x, 0, z );
						
						if ( args[ 1 ].equalsIgnoreCase( "add" ) ) {
							MythicMeteors.getPlugin( MythicMeteors.class ).getLocations().add( location );
							sender.sendMessage( "&1[&9MythicMeteor&1] &aAdded location successfully!".replace( '&', '\u00a7' ) );
						} else {
							MythicMeteors.getPlugin( MythicMeteors.class ).getLocations().remove( location );
							sender.sendMessage( "&1[&9MythicMeteor&1] &aRemoved location successfully!".replace( '&', '\u00a7' ) );
						}
						
					} catch ( Exception exception ) {
						sender.sendMessage(  "&1[&9MythicMeteor&1]".replace( '&', '\u00a7' ) + ChatColor.RED + " Location format must be '<world>;<x>;<z>'!" );
					}
				} else if ( args[ 2 ].equalsIgnoreCase( "rewards" ) ) {
					RewardSet set = EventManager.getInstance().getRewardSet( args[ 3 ] );
					if ( set == null ) {
						sender.sendMessage( "&1[&9MythicMeteor&1] &cInvalid reward!".replace( '&', '\u00a7' ) );
						return;
					}
					
					if ( args[ 1 ].equalsIgnoreCase( "add" ) ) {
						MythicMeteors.getPlugin( MythicMeteors.class ).getRewards().add( args[ 3 ] );
						sender.sendMessage( "&1[&9MythicMeteor&1] &aAdded reward successfully!".replace( '&', '\u00a7' ) );
					} else {
						MythicMeteors.getPlugin( MythicMeteors.class ).getRewards().remove( args[ 3 ] );
						sender.sendMessage( "&1[&9MythicMeteor&1] &aRemoved reward successfully!".replace( '&', '\u00a7' ) );
					}
				} else {
					sender.sendMessage( "&1[&9MythicMeteor&1] &cInvalid property! Valid ones are bosses, locations, and rewards! Perhaps you want 'set'?".replace( '&', '\u00a7' ) );
				}
			}
		}
	}
}
