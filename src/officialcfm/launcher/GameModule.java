package officialcfm.launcher;

import java.io.File;
import java.io.IOException;

/**
 * Launches the game itself, honestly it just calls a batch file
 * because I was too lazy to load all the libraries used by
 * Minecraft myself :/
 * 
 * @author KP
 */
public class GameModule implements IModule {
	private String accessToken = "", clientToken = "",
			       playerId = "", playerName = "";
	
	public Process gameProcess;
	
	@Override
	public void init() {
		try {
			File batchFile = new File(FileModule.DATA_PATH + "/etc/launch.bat");
			String batchString = batchFile.getAbsolutePath();
			String batchDebug = Launcher.DEBUG ? "" : "/B";
			
			gameProcess = Runtime.getRuntime().exec("cmd /c \"" +
													batchString + "\" " +
													accessToken + " " +
													clientToken + " " +
													playerId + " " +
													playerName + " " +
													batchDebug,
													null, batchFile.getParentFile());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	public String getPlayerId() {
		return playerId;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}
