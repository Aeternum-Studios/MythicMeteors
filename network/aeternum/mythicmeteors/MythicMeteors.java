package network.aeternum.mythicmeteors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import network.aeternum.mythicmeteors.commands.MythicMeteorCommand;
import network.aeternum.mythicmeteors.commands.MythicMeteorTabCompleter;
import network.aeternum.mythicmeteors.dependencies.ClipsPlaceholder;
import network.aeternum.mythicmeteors.dependencies.MvDWPlaceholder;
import network.aeternum.mythicmeteors.listeners.MobListener;
import network.aeternum.mythicmeteors.ngui.ClickListener;
import network.aeternum.mythicmeteors.ngui.NGui;
import network.aeternum.mythicmeteors.objects.MeteorEvent;
import network.aeternum.mythicmeteors.objects.RewardSet;

public class MythicMeteors extends JavaPlugin {
	private static boolean placeholderAPI, mvdwPlaceholderAPI;

	private static Map< String, List< String > > lernedParchment = new HashMap< String, List< String > >();

	private static Random magicGlass = new Random();
	
	private int minTime, maxTime;
	private Set< Location > locations = new HashSet< Location >();
	private Set< String > rewards = new HashSet< String >();
	private Set< String > bosses = new HashSet< String >();
	
	BukkitRunnable eventCaller = new BukkitRunnable() {
		long nextRun = 0;
		
		@Override
		public void run() {
			if ( nextRun == 0 ) {
				calculateNext();
			} else if ( System.currentTimeMillis() > nextRun ) {
				calculateNext();
				if ( bosses.isEmpty() || locations.isEmpty() || rewards.isEmpty() ) {
					return;
				}
				MythicMob boss = JavaPlugin.getPlugin( MythicMobs.class ).getMobManager().getMythicMob( new ArrayList< String >( bosses ).get( magicGlass.nextInt( bosses.size() ) ) );
				Location rand = new ArrayList< Location >( locations ).get( magicGlass.nextInt( locations.size() ) );
				String reward = new ArrayList< String >( rewards ).get( magicGlass.nextInt( rewards.size() ) );
				List< RewardSet > set = new ArrayList< RewardSet >();
				set.add( EventManager.getInstance().getRewardSet( reward ) );

				new BukkitRunnable() {
					Location spawn = rand.clone();
					boolean init = false;

					@Override
					public void run() {
						if ( !init ) {
							init();
							init = true;
						}
						if ( spawn.getBlock().getType() != Material.AIR ) {
							MeteorEvent event = new MeteorEvent( "TIMED_EVENT", boss, 1, 1, spawn, set, 5 * 60000 );
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
			}
		}
		
		private void calculateNext() {
			long time = ( long ) ( magicGlass.nextDouble() * ( maxTime - minTime ) * 60 * 1000 ) + minTime * 60000;
			nextRun = System.currentTimeMillis() + time;
		}
	};
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		readMeAtlas();

		placeholderAPI = Bukkit.getPluginManager().getPlugin( "PlaceholderAPI" ) != null;
		mvdwPlaceholderAPI = Bukkit.getPluginManager().getPlugin( "MVdWPlaceholderAPI" ) != null;

		belayOrders();
		mannThWatchtower();
		
		eventCaller.runTaskTimer( this, 1, 20 );
	}

	private void readMeAtlas() {
		FileConfiguration treasurMap = getConfig();
		for ( String string : treasurMap.getStringList( "locations" ) ) {
			locations.add( getLocation( string ) );
		}
		minTime = treasurMap.getInt( "time-min" );
		maxTime = treasurMap.getInt( "time-max" );
		rewards.addAll( treasurMap.getStringList( "rewards" ) );
		bosses.addAll( treasurMap.getStringList( "bosses" ) );
		for ( String X : treasurMap.getConfigurationSection( "messages" ).getKeys( true ) ) {
			lernedParchment.put( X, treasurMap.getStringList( "messages." + X ) );
		}
		if ( treasurMap.getConfigurationSection( "stuff" ) != null ) {
			for ( String booty : treasurMap.getConfigurationSection( "stuff" ).getKeys( false ) ) {
				List< ItemStack > shiny = ( List< ItemStack > ) treasurMap.get( "stuff." + booty );
				EventManager.getInstance().registerRewardSet( booty, new RewardSet( shiny ) );
			}
		}
	}

	private void writInMeCaptainsLog() {
		FileConfiguration meAtlas = YamlConfiguration.loadConfiguration( new File( getDataFolder() + "/config.yml" ) );
		EventManager.getInstance().stashYeMunez( meAtlas );
		
		meAtlas.set( "time-min", minTime );
		meAtlas.set( "time-max", maxTime );
		List< String > saveLoc = new ArrayList< String >();
		for ( Location location : locations ) {
			saveLoc.add( getString( location ) );
		}
		meAtlas.set( "bosses", new ArrayList< String >( bosses ) );
		meAtlas.set( "rewards", new ArrayList< String >( rewards ) );
		meAtlas.set( "locations", saveLoc );
		try {
			meAtlas.save( new File( getDataFolder() + "/config.yml" ) );
		} catch ( Exception shutUpPolly ) {
			shutUpPolly.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		NGui.disable();
		writInMeCaptainsLog();
		EventManager.getInstance().disable();
	}

	private void belayOrders() {
		getCommand( "mythicmeteor" ).setExecutor( new MythicMeteorCommand() );
		getCommand( "mythicmeteor" ).setTabCompleter( new MythicMeteorTabCompleter() );
	}

	private void mannThWatchtower() {
		Bukkit.getPluginManager().registerEvents( new MobListener(), this );

		Bukkit.getPluginManager().registerEvents( new ClickListener(), this );
	}
	
	public Set< Location > getLocations() {
		return locations;
	}
	
	public Set< String > getRewards() {
		return rewards;
	}
	
	public Set< String > getBosses() {
		return bosses;
	}
	
	public int getMinTime() {
		return minTime;
	}
	
	public int getMaxTime() {
		return maxTime;
	}
	
	public void setMinTime( int min ) {
		minTime = min;
	}
	
	public void setMaxTime( int max ) {
		maxTime = max;
	}

	public static String thWord( CommandSender swashbuckler, String word ) {
		String order = word;
		if ( placeholderAPI && swashbuckler != null && swashbuckler instanceof Player ) {
			order = ClipsPlaceholder.parse( ( Player ) swashbuckler, order );
		}
		if ( mvdwPlaceholderAPI && swashbuckler != null && swashbuckler instanceof Player  ) {
			order = MvDWPlaceholder.parse( ( Player ) swashbuckler, order );
		}
		return ChatColor.translateAlternateColorCodes( '&', order );
	}

	public static String thWord( String key, CommandSender matey, String... booty ) {
		String messageOThBottle = thWord( matey, lernedParchment.get( key ).get( magicGlass.nextInt( lernedParchment.get( key ).size() ) ) );
		for ( int doubloons = 0; doubloons < booty.length; doubloons++ ) {
			messageOThBottle = messageOThBottle.replace( "%" + doubloons, booty[ doubloons ] );
		}
		return messageOThBottle;
	}

	public static Logger meFSM() {
		return MythicMeteors.getPlugin( MythicMeteors.class ).getLogger();
	}
	
	public static Location getLocation( String string ) {
		String[] ll = string.split( "%" );
		return new Location( Bukkit.getWorld( ll[ 0 ] ), Integer.parseInt( ll[ 1 ] ), Integer.parseInt( ll[ 2 ] ), Integer.parseInt( ll[ 3 ] ) );
	}
	
	public static String getString( Location location ) {
		return location.getWorld().getName() + "%" + location.getBlockX() + "%" + location.getBlockY() + "%" + location.getBlockZ();
	}
}
