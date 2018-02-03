package officialcfm.launcher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.JSONObject;

import officialcfm.launcher.AuthenticationProvider.AuthState;
import officialcfm.launcher.LogModule.Case;

/**
 * Sets up a `JFrame` that contains the launcher itself
 * WARNING: *VERY* fucking messy
 * 
 * @author KP
 */
public class WindowModule implements IModule {
	private static final int WIDTH = 640, HEIGHT = 480;
	private JFrame frame;
	
	@Override
	public void init() {
		frame = new JFrame(Launcher.NAME + " " + Launcher.VERSION);
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.BLACK);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		ImagePane imagePane = null;
		
		try {
			imagePane = new ImagePane(ImageIO.read(WindowModule.class.getResource("/resources/bg.png")), ImageIO.read(WindowModule.class.getResource("/resources/ov.png")));
			
			frame.setIconImage(ImageIO.read(WindowModule.class.getResource("/resources/ico.png")));
			frame.setContentPane(imagePane);
		} catch (IOException e) {
			Launcher.logModule.log(Case.ERROR, "Failed to load background image.");
			e.printStackTrace();
		}
		
		Container pane = frame.getContentPane();
        pane.setLayout(null);
        
        JTextField f1 = new JTextField();
        JPasswordField f2 = new JPasswordField();
        
        File loginFile = new File(FileModule.LOGIN_PATH);
        String loginString = "";
        String passString = "";
        
        if (loginFile.exists()) {
            try {
    			Scanner loginScanner = new Scanner(loginFile);
    			
    			if (loginScanner.hasNextLine()) {
    				loginString = loginScanner.nextLine();
    			}

    			if (loginScanner.hasNextLine()) {
    				passString = loginScanner.nextLine();
    			}
    			
    			f1.setText(loginString);
    			f2.setText(passString);
    			
    			loginScanner.close();
    		} catch (FileNotFoundException e1) {
    			e1.printStackTrace();
    		}
        }
        
        JLabel user = new JLabel("Username / E-mail");
        user.setForeground(Color.WHITE);
        user.setFont(user.getFont().deriveFont(1));
        
        JLabel sUser = new JLabel(user.getText());
        sUser.setForeground(Color.BLACK.brighter());
        sUser.setFont(user.getFont().deriveFont(1));
        
        JLabel pass = new JLabel("Password");
        pass.setForeground(Color.WHITE);
        pass.setFont(pass.getFont().deriveFont(1));

        JButton b1 = new JButton("Launch");
        b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AuthenticationProvider ap = new AuthenticationProvider();
				String accessToken = "", clientToken = "", id = "", name = "";
				
				try {
					String result = ap.performHttpRequest(new URL("https://authserver.mojang.com/authenticate"),
							        ap.performLoginRequest(f1.getText(), new String(f2.getPassword())));
					
					Launcher.logModule.log(Case.INFO, "Response from Mojang: " + result);
					
					if (ap.getAuthState() != AuthState.FAILURE) {
						FileWriter writer = new FileWriter(loginFile);
						PrintWriter printer = new PrintWriter(writer);
						printer.println(f1.getText());
						printer.println(f2.getPassword());
						printer.close();
						
						JSONObject jsonResult = new JSONObject(result);
						accessToken = jsonResult.getString("accessToken");
						clientToken = jsonResult.getString("clientToken");
						
						JSONObject jsonProfile = jsonResult.getJSONObject("selectedProfile");
						id = jsonProfile.getString("id");
						name = jsonProfile.getString("name");

						Launcher.gameModule.setAccessToken(accessToken);
						Launcher.gameModule.setClientToken(clientToken);
						Launcher.gameModule.setPlayerId(id);
						Launcher.gameModule.setPlayerName(name);
						Launcher.gameModule.init();
						
						Launcher.logModule.log(Case.INFO, "Starting game. Be sure to check crash logs in your \".megaplex\" folder!");
						System.exit(0);
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
        
        JLabel sPass = new JLabel(pass.getText());
        sPass.setForeground(Color.BLACK.brighter());
        sPass.setFont(user.getFont().deriveFont(1));
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuLauncher = new JMenu("Launcher");
        menuLauncher.add(new AbstractAction("Exit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
        });
        
        JMenu menuMemory = new JMenu("Memory");
        menuMemory.add(new AbstractAction("Set Memory Options") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Implement this.
			}
        });
        
        JMenu menuUpdate = new JMenu("Updates");
        menuUpdate.add(new AbstractAction("Force Reset") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
		        if (JOptionPane.showConfirmDialog(frame, 
			            "Are you sure you want to reset?\nThis will remove any client-side mods installed.", frame.getTitle(), 
			            JOptionPane.YES_NO_OPTION,
			            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
		        			// TODO: Implement this.
			        }
			}
        });

        menuBar.add(menuLauncher);
        
        /*
        	menuBar.add(menuMemory);
        	menuBar.add(menuUpdate);
        	^ include these once they're implemented
		*/

        JEditorPane jep = new JEditorPane();
        jep.setEditable(false);
        jep.setBackground(new Color(0, 0, 0, 0));
        jep.setForeground(Color.WHITE);
        jep.setOpaque(false);
        jep.setBorder(null);
        
        JScrollPane jsp = new JScrollPane(jep);
        jsp.getViewport().setOpaque(false);
        jsp.setBackground(new Color(0, 0, 0, 0));
        jsp.setOpaque(false);
        jsp.setBorder(null);
        jsp.setViewportBorder(null);
        
        try {
        	jep.setPage(FileModule.CHANGELOG_PATH);
        } catch (IOException e) {
        	Launcher.logModule.log(Case.WARNING, "Failed to load changelog!");
        	
        	jep.setContentType("text/html");
        	jep.setText("<html>Could not load changelog.</html>");
        	
        	e.printStackTrace();
        }
        
        pane.add(b1);
        pane.add(menuBar);
        pane.add(f1);
        pane.add(f2);
        pane.add(user);
        pane.add(sUser);
        pane.add(pass);
        pane.add(sPass);
        pane.add(jsp);
        
        Insets insets = pane.getInsets();
        Dimension size = b1.getPreferredSize();
        
        b1.setBounds(
        		getWidth() - size.width - insets.right - 20,
        		getHeight() - size.height - insets.bottom - size.height - 16,
        		size.width, size.height);
        
        size = f2.getPreferredSize();
        size.width = 255;
        
        f2.setBounds(
        		getWidth() - size.width - insets.right - 100,
        		getHeight() - size.height - insets.bottom - size.height - 16,
        		size.width, size.height);
        
        f1.setBounds(
        		getWidth() - size.width - insets.right - (size.width + 116),
        		getHeight() - size.height - insets.bottom - size.height - 16,
        		size.width, size.height);
        
        sUser.setBounds(f1.getX() + 2, 1 + f1.getY() - user.getPreferredSize().height, user.getPreferredSize().width, user.getPreferredSize().height);
        sPass.setBounds(f2.getX() + 2, 1 + f2.getY() - pass.getPreferredSize().height, pass.getPreferredSize().width, pass.getPreferredSize().height);
        user.setBounds(f1.getX() + 2, f1.getY() - user.getPreferredSize().height, user.getPreferredSize().width, user.getPreferredSize().height);
        pass.setBounds(f2.getX() + 2, f2.getY() - pass.getPreferredSize().height, pass.getPreferredSize().width, pass.getPreferredSize().height);
        
		menuBar.setBounds(insets.left, insets.top, getWidth(), menuBar.getPreferredSize().height);
		
        jsp.setBounds(0, menuBar.getPreferredSize().height, getWidth() - jsp.getVerticalScrollBar().getWidth() - 6, 361);
        
		frame.setVisible(true);
		
		Launcher.logModule.log(Case.INFO, "Window module initialized.");
	}
	
	public int getWidth() {
		return frame.getWidth();
	}
	
	public int getHeight() {
		return frame.getHeight();
	}
}
