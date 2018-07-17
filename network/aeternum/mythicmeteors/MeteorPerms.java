package network.aeternum.mythicmeteors;

import org.bukkit.permissions.Permissible;

public class MeteorPerms {
	public static boolean beYeCapn( Permissible matey ) {
		return matey.hasPermission( "mythicmeteors.admin" );
	}
	
	public static boolean beYeFirsMate( Permissible matey ) {
		return matey.hasPermission( "mythicmeteors.manage" ) || beYeCapn( matey );
	}
}
