package officialcfm.launcher;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

import org.json.JSONObject;

/**
 * Communicates with Mojang's authentication servers to get
 * all the necessary session info to launch the game
 * 
 * @author KP
 */
public class AuthenticationProvider {
	public enum AuthState {
		SUCCESS, NEUTRAL, FAILURE
	}
	
	private AuthState authState;
	
	public String performLoginRequest(String username, String password) {
		JSONObject json1 = new JSONObject();
		json1.put("name", "Minecraft");
		json1.put("version", 1);
		
		JSONObject json = new JSONObject();
		json.put("agent", json1);
		json.put("username", username);
		json.put("password", password);
		
		return json.toString();
	}
	
	public String performHttpRequest(URL url, String content) throws Exception {
        byte[] contentBytes = content.getBytes("UTF-8");

        URLConnection connection = url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", Integer.toString(contentBytes.length));

        OutputStream requestStream = connection.getOutputStream();
        requestStream.write(contentBytes, 0, contentBytes.length);
        requestStream.close();

        String response = "";
        BufferedReader responseStream;
        
        if (((HttpURLConnection) connection).getResponseCode() == 200) {
            responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        } else {
            responseStream = new BufferedReader(new InputStreamReader(((HttpURLConnection) connection).getErrorStream(), "UTF-8"));
        }

        response = responseStream.readLine();
        responseStream.close();

        if (((HttpURLConnection) connection).getResponseCode() != 200) {
        	JSONObject json = new JSONObject(response);
        	String k = json.getString("errorMessage");
        	
			JOptionPane.showMessageDialog(null, "Error: " + k, "Authentication Error", JOptionPane.ERROR_MESSAGE);
			
			authState = AuthState.FAILURE;
        } else {
        	authState = AuthState.SUCCESS;
        }

        return response;
	}
	
	public void updateState() {
		if (authState == AuthState.NEUTRAL) {
			authState = AuthState.SUCCESS;
		}
	}
	
	public AuthState getAuthState() {
		return authState;
	}
}
