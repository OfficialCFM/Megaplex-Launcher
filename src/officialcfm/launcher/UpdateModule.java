package officialcfm.launcher;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import officialcfm.launcher.LogModule.Case;

/**
 * Used for gathering new versions of the modpack, and also for downloading
 * the modpack's basefiles if they aren't found
 * 
 * @author KP
 */
public class UpdateModule implements IModule {
	private boolean hasUpdates;
	private boolean firstSetup = false;
	private String currentVersion, latestVersion;
	
	@Override
	public void init() {
		try {
			File versionLocal = new File(FileModule.DATA_PATH + "/megaplex-version.txt");

			if (!new File(versionLocal.toURI()).exists()) {
				firstSetup = true;
				Launcher.logModule.log(Case.WARNING, "No version file found. Perform first time setup?");
				return;
			}
			
			Scanner versionLocalScanner = new Scanner(versionLocal);
			String versionLocalString = versionLocalScanner.nextLine();
			
			URL versionUrl = new URL(FileModule.VERSION_PATH);
			Scanner versionScanner = new Scanner(versionUrl.openStream());
			String versionString = versionScanner.nextLine();
			
			if (versionString != null) {
				currentVersion = versionLocalString;
				latestVersion = versionString;
				
				double newVersionDouble = Double.parseDouble(versionString.replaceAll("v", ""));
				double localVersionDouble = Double.parseDouble(versionLocalString.replaceAll("v", ""));
				
				if (newVersionDouble > localVersionDouble) {
					hasUpdates = true;
				}
			} else {
				Launcher.logModule.log(Case.WARNING, "Failed to locate version from " + FileModule.VERSION_PATH + "!");
				Launcher.logModule.log(Case.WARNING, "You may be running an outdated version of Megaplex.");
			}
			
			versionLocalScanner.close();
			versionScanner.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasNoUpdates() {
		return !hasUpdates;
	}
	
	public boolean isFirstSetup() {
		return firstSetup;
	}
	
	public String getCurrentVersion() {
		return currentVersion;
	}
	
	public String getLatestVersion() {
		return latestVersion;
	}
}
