package officialcfm.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import officialcfm.launcher.LogModule.Case;

/**
 * Execute file download in a background thread and update the progress.
 * This code has been extended to download a zip file, used by the launcher to
 * pack/unpack updates and/or the modpack's basefiles
 * 
 * @author www.codejava.net
 * @author KP
 */
public class DownloadTask extends SwingWorker<Void, Void> {
	private static final int BUFFER_SIZE = 4096;
	private String downloadURL;
	private String saveDirectory;
	private WindowUpdateModule gui;
	private String name;

	public DownloadTask(WindowUpdateModule gui, String downloadURL, String saveDirectory, String name) {
		this.gui = gui;
		this.downloadURL = downloadURL;
		this.saveDirectory = saveDirectory;
		this.name = name;
	}

	/**
	 * Executed in background thread
	 */
	@Override
	protected Void doInBackground() throws Exception {
		try {
			HttpDownloadUtility util = new HttpDownloadUtility();
			util.downloadFile(downloadURL);

			// set file information on the GUI
			gui.setFileInfo(util.getFileName(), util.getContentLength());

			String saveFilePath = saveDirectory + File.separator + util.getFileName();

			InputStream inputStream = util.getInputStream();
			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			long totalBytesRead = 0;
			int percentCompleted = 0;
			long fileSize = util.getContentLength();

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				totalBytesRead += bytesRead;

				gui.setProgressSize(totalBytesRead);
				percentCompleted = (int) (totalBytesRead * 100 / fileSize);

				setProgress(percentCompleted);
			}

			outputStream.close();

			util.disconnect();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(gui.frame, "Error downloading file: " + ex.getMessage(), gui.frame.getTitle(),
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			setProgress(0);
			cancel(true);
		}
		return null;
	}

	/**
	 * Executed in Swing's event dispatching thread
	 */
	@Override
	protected void done() {
		if (!isCancelled()) {
			JOptionPane.showMessageDialog(
					gui.frame,
					"File has been downloaded successfully!",
					gui.frame.getTitle(),
					JOptionPane.INFORMATION_MESSAGE);

			File zipFile = new File(FileModule.DATA_PATH + "/" + name);
			UnzipUtility zipUtil = new UnzipUtility();
			
			try {
				zipUtil.unzip(zipFile.getAbsolutePath(), zipFile.getParentFile().getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			zipFile.delete();
			
			if (name == "megaplex.zip") {
				gui.frame.dispose();
				Launcher.windowModule.init();
			} else if (name == "megaplex-base.zip") {
				gui.frame.dispose();
				Launcher.updateModule.init();
				Launcher.logModule.log(Case.INFO, "New version available!");
				Launcher.windowsUpdateModule.path = FileModule.DOWNLOAD_PATH;
				Launcher.windowsUpdateModule.init();
			}
		}
	}
}
