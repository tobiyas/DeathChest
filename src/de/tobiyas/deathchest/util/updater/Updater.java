/**
 */

package de.tobiyas.deathchest.util.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.tobiyas.deathchest.DeathChest;


public class Updater{

	private double versionNO;
	private int subVersionNO;
	private String source;
	private DeathChest plugin;
	
	private URL url;
	private String downloadLink;
	
	/**
	 *
	 * @param website the url to the Info site
	 */
	public Updater(String website){
		
		plugin = DeathChest.getPlugin();
		
		try{
			url = new URL(website);
		} catch(MalformedURLException ex){
			ex.printStackTrace();
			plugin.log("Updater: Malformed URL Exception. Make sure the URL is in the form 'http://www.website.domain'");
		}	
		
	}
	
	
	/**
	 * Check the current version against te latest one.
	 * @param currentVersion - Double
	 * @param currentSubVersion - Double
	 * @param pluginname - String
	 * @return
	 */
	public boolean checkVersion(double currentVersion, int currentSubVersion){
		
		source = Fetcher.fetchSource(url);
		formatSource(source);
		
		if(versionNO > currentVersion || (versionNO == currentVersion && subVersionNO > currentSubVersion)){
			plugin.log("Updater: You are not running the latest version!");
			plugin.log("Updater: Running version : " + currentVersion + "_" + currentSubVersion + ". Latest version : " + versionNO + "_" + subVersionNO + ".");
			return false;
		}
		
		if(versionNO < currentVersion || (versionNO == currentVersion && subVersionNO < currentSubVersion)){
			plugin.log("Updater: Your version is higher than the actual.");
			return true;
		}
		
		if(versionNO == currentVersion && subVersionNO == currentSubVersion)
			return true;
		
		return false;
		
	}
	
	/**
	 * Force a download of the newest version
	 * 
	 * @param downloadLink - String
	 * @param pluginName - String
	 */
	public boolean forceDownload(){
		String pluginName = plugin.getDescription().getName();
		
		File downloadPath = plugin.getServer().getUpdateFolderFile();
				
		plugin.log("Updater: Downloading newest version of " + pluginName + "...");
		
		try{
			BufferedInputStream in = new java.io.BufferedInputStream(new 
					 
			URL(downloadLink).openStream());
			FileOutputStream fos = new java.io.FileOutputStream(downloadPath);
			BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
			byte[] data = new byte[1024];
			int x=0;
			
			while((x=in.read(data,0,1024))>=0){
				bout.write(data,0,x);
			}
			
			bout.close();
			in.close();
		} catch (Exception ex){
			ex.printStackTrace();
			plugin.log("Updater: Error while downloading update!");
		}
		plugin.log("Updater: Download completed successfully!");
		
		return true;
	}
	
	private void formatSource(String source){
		String parts[] = source.split("\\@");
		
		try{
			versionNO = Double.parseDouble(parts[1]);
			subVersionNO = Integer.parseInt(parts[2]);
			downloadLink = parts[3];
		} catch (NumberFormatException ex){
			ex.printStackTrace();
			plugin.log("Updater: Error while parsing Update Info!");
		}
	}

}
