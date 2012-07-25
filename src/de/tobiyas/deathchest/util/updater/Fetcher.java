/**
 */

package de.tobiyas.deathchest.util.updater;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import de.tobiyas.deathchest.DeathChest;

public class Fetcher {

	protected static String fetchSource(URL website){
		DeathChest plugin = DeathChest.getPlugin();
		
	    InputStream is = null;
	    DataInputStream dis = null;
	    String s, source = "";

		try {
			is = website.openStream();
		} catch (IOException ex) {
			ex.printStackTrace();
			plugin.log("Updater: Error opening URL input stream!");
		}
		
	    dis = new DataInputStream(new BufferedInputStream(is));
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		
		try {
			while ((s = br.readLine()) != null) {
			    source += s;
			 }
		} catch (IOException ex) {
			ex.printStackTrace();
			plugin.log("Updater: Error reading input stream!");
		}
		
		try {
            is.close();
         } catch (IOException ioe) {
        	 ioe.printStackTrace();
        	 plugin.log("Updater: Error closing URL input stream!");
         }
         
		return source;
	}
	
	
}
