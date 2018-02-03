package officialcfm.launcher;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import officialcfm.launcher.LogModule.Case;

/**
 * Starts and stops every module
 * 
 * @author KP
 */
public class Launcher {
	public static final String NAME = "Megaplex Launcher";
	public static final String VERSION = "v0.1.2";
	public static final boolean DEBUG = false; // Disable before release
	
	public static LogModule logModule = new LogModule();
	public static WindowModule windowModule = new WindowModule();
	public static FileModule fileModule = new FileModule();
	public static GameModule gameModule = new GameModule();
	public static UpdateModule updateModule = new UpdateModule();
	public static WindowUpdateModule windowsUpdateModule = new WindowUpdateModule();
	
	public static void main(String[] args)  {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					UIManager.getLookAndFeelDefaults().put("defaultFont", new Font("Trebuchet MS", Font.PLAIN, 12));
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logModule.init();
		fileModule.init();
		updateModule.init();
		
		if (updateModule.hasNoUpdates() && !updateModule.isFirstSetup()) {
			windowModule.init();
		} else {
			if (updateModule.isFirstSetup()) {
				logModule.log(Case.INFO, "Performing first-time setup!");
				windowsUpdateModule.path = FileModule.BASEFILES_PATH;
				windowsUpdateModule.init();
			} else {
				logModule.log(Case.INFO, "New version available!");
				windowsUpdateModule.path = FileModule.DOWNLOAD_PATH;
				windowsUpdateModule.init();
			}
		}
	}
}
