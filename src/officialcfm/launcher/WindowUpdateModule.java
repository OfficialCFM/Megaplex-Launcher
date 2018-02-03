package officialcfm.launcher;

import java.awt.Container;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Issa progress bar
 * 
 * @author KP
 */
public class WindowUpdateModule implements IModule, PropertyChangeListener {
	public JFrame frame = null;
	public JProgressBar jpb = null;
	public JLabel downloadLabel = null;
	public JLabel progressLabel = null;
	public String path = FileModule.DOWNLOAD_PATH;
	
	@Override
	public void init() {
		String name = (path == FileModule.DOWNLOAD_PATH) ? "megaplex.zip" : "megaplex-base.zip";
		DownloadTask dt = new DownloadTask(this, path, FileModule.DATA_PATH, name);
		
		frame = new JFrame("Download progress");
		frame.setResizable(false);
		frame.setSize(480, 124);
		frame.setLocationRelativeTo(null);
		
		try {
			frame.setIconImage(ImageIO.read(WindowUpdateModule.class.getResource("/resources/ico.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(
		        	frame, 
		            "Are you sure you want to cancel the download?",
		            frame.getTitle(), 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
		        		dt.cancel(true);
		        		frame.dispose();
		    			Launcher.windowModule.init();
		        }
		    }
		});
		
		if (path == FileModule.DOWNLOAD_PATH) {
			Object[] options = { "Yes", "No" };
			int option = JOptionPane.showOptionDialog(
					frame,
					"New Megaplex version available. " +
					"Would you like to update?\n" +
					"You are currently running " +
					Launcher.updateModule.getCurrentVersion() +
					".\nThe latest version is " +
					Launcher.updateModule.getLatestVersion() + ".",
					"Megaplex update",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null, options, options[0]);
			
			if (option == 1) {
				frame.dispose();
				Launcher.windowModule.init();
			} else if (option == 0) {
				Container pane = frame.getContentPane();
		        pane.setLayout(null);
		        
		        Insets insets = pane.getInsets();
		        
		        jpb = new JProgressBar();
		        jpb.setValue(0);
		        jpb.setStringPainted(true);
		        jpb.setBounds(insets.left + 16, insets.top + 16, frame.getWidth() - 38, jpb.getPreferredSize().height);
		        
		        downloadLabel = new JLabel("Downloading " + fileName);
		        downloadLabel.setBounds(insets.left + 16, insets.top + 16 + jpb.getPreferredSize().height + 16, downloadLabel.getPreferredSize().width, downloadLabel.getPreferredSize().height);
		        
		        progressLabel = new JLabel("Receiving " + progressSize + " / " + fileSize);
		        progressLabel.setBounds(insets.left + 16, downloadLabel.getY() + 16, progressLabel.getPreferredSize().width, progressLabel.getPreferredSize().height);
		        
		        pane.add(jpb);
				pane.add(downloadLabel);
				pane.add(progressLabel);
				
				dt.addPropertyChangeListener(this);
				dt.execute();
				
				frame.setVisible(true);
			}
		} else if (path == FileModule.BASEFILES_PATH) {
			Object[] options = { "Ok" };
			int option = JOptionPane.showOptionDialog(frame, "Megaplex is not installed. Hit 'Ok' to perform first-time setup.", "Megaplex update", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			
			if (option == 0) {
				Container pane = frame.getContentPane();
		        pane.setLayout(null);
		        
		        Insets insets = pane.getInsets();
		        
		        jpb = new JProgressBar();
		        jpb.setValue(0);
		        jpb.setStringPainted(true);
		        jpb.setBounds(insets.left + 16, insets.top + 16, frame.getWidth() - 38, jpb.getPreferredSize().height);
		        
		        downloadLabel = new JLabel("Downloading " + fileName);
		        downloadLabel.setBounds(insets.left + 16, insets.top + 16 + jpb.getPreferredSize().height + 16, downloadLabel.getPreferredSize().width, downloadLabel.getPreferredSize().height);
		        
		        progressLabel = new JLabel("Receiving " + progressSize + " / " + fileSize);
		        progressLabel.setBounds(insets.left + 16, downloadLabel.getY() + 16, progressLabel.getPreferredSize().width, progressLabel.getPreferredSize().height);
		        
		        pane.add(jpb);
				pane.add(downloadLabel);
				pane.add(progressLabel);
				
				dt.addPropertyChangeListener(this);
				dt.execute();
				
				frame.setVisible(true);
			}
		}
	}
	
	private String fileName;
	private int fileSize;
	private long progressSize;

	public void setFileInfo(String fileName, int fileSize) {
		this.fileName = fileName;
		this.fileSize = fileSize;
	}

	public void setProgressSize(long progressSize) {
		this.progressSize = progressSize;
	}

    public static String floatForm(double d) {
       return new DecimalFormat("#.##").format(d);
    }

    public static String bytesToHuman(long size) {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " KiB";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " MiB";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " GiB";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " TiB";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " PiB";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " EiB";

        return "???";
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("progress")) {
			Container pane = frame.getContentPane();
	        Insets insets = pane.getInsets();
	        
			int progress = (Integer) evt.getNewValue();
			jpb.setValue(progress);
			downloadLabel.setText("Downloading " + fileName + " from " + path);
			downloadLabel.setBounds(insets.left + 16, insets.top + 16 + jpb.getPreferredSize().height + 16, downloadLabel.getPreferredSize().width, downloadLabel.getPreferredSize().height);
	        
			progressLabel.setText("Receiving " + bytesToHuman(progressSize) + " / " + bytesToHuman(fileSize));
			progressLabel.setBounds(insets.left + 16, downloadLabel.getY() + 16, progressLabel.getPreferredSize().width, progressLabel.getPreferredSize().height);
		}
	}
}
