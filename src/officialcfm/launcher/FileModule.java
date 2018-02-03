package officialcfm.launcher;

import java.io.File;

import officialcfm.launcher.LogModule.Case;

/**
 * Contains all paths and URLs required by the launcher, also responsible for
 * creating the `.megaplex` folder in %APPDATA%, similar to `.minecraft`
 * 
 * @author KP
 */
public class FileModule implements IModule {
	/** Where all of the megaplex files go, similar to `.minecraft` for vanilla */
	public static final String DATA_PATH = System.getenv("APPDATA") + "/.megaplex";
	
	/** The batch file that starts the game */
	public static final String BATCH_PATH = "/launch.bat";
	
	/** The latest version of megaplex */
	public static final String DOWNLOAD_PATH = "http://mc.kpworld.xyz/megaplex.zip";
	
	/** The megaplex base files (the game itself, + forge) */
	public static final String BASEFILES_PATH = "http://mc.kpworld.xyz/megaplex-base.zip";
	
	/** The latest version of megaplex */
	public static final String VERSION_PATH = "http://mc.kpworld.xyz/version.txt";
	
	/** The changelog */
	public static final String CHANGELOG_PATH = "http://mc.kpworld.xyz/updates.html";
	
	/** Where the login information is stored */
	public static final String LOGIN_PATH = DATA_PATH + "/megaplex-login.txt";
	
	@Override
	public void init() {
		File dataFolder = new File(DATA_PATH); 
		
		if (!dataFolder.exists()) {
			Launcher.logModule.log(Case.WARNING, "Megaplex directory does not exist!");
			Launcher.logModule.log(Case.INFO, "Megaplex game directory: \"" + dataFolder.getPath() + "\"");
			
			boolean created = false;
			
			try {
				dataFolder.mkdir();
				created = true;
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			
			if (created) {
				Launcher.logModule.log(Case.INFO, "Megaplex directory successfully created.");
			}
		}
	}
}
