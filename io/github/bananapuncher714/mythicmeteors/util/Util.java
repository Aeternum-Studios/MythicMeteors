package io.github.bananapuncher714.mythicmeteors.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Util {

	public static < T > T getRandom( Map< T, Integer > objects ) {
		int sum = 0;
		for ( int i : objects.values() ) {
			sum = sum + i;
		}
		List< T > items = new ArrayList< T >( objects.keySet() );
		int randomIndex = -1;
		double random = Math.random() * sum;
		for ( int i = 0; i < items.size(); ++i ) {
		    random -= objects.get( items.get( i ) );
		    if (random <= 0.0d )  {
		        randomIndex = i;
		        break;
		    }
		}
		return items.get( randomIndex );
	}
}
