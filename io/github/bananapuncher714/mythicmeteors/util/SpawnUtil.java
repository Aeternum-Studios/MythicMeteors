package io.github.bananapuncher714.mythicmeteors.util;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

public class SpawnUtil {
	private static Random RAND = new Random();
	
	public static Location getRandomSpawn( Location location, int maxRange, int minRange ) {
		World world = location.getWorld();
		double degree = RAND.nextDouble() * 360;
		double x = Math.cos( Math.toRadians( degree ) );
		double z = Math.sin( Math.toRadians( degree ) );
		double range = RAND.nextDouble() * ( maxRange - minRange );
		Location spawnLoc = new Location( world, x * ( range + minRange ) + location.getX(), location.getY(), z * ( range + minRange ) + location.getZ() );
		spawnLoc.setY( world.getHighestBlockYAt( spawnLoc ) );
		return spawnLoc;
	}

}
