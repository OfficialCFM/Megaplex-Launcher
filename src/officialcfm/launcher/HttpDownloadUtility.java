package officialcfm.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import officialcfm.launcher.LogModule.Case;

/**
 * Used for downloading the modpack zip files
 * 
 * @author KP
 */
public class HttpDownloadUtility {
	private HttpURLConnection httpConn;
	private InputStream inputStream;
	private String fileName;
	private int contentLength;

	public void downloadFile(String fileURL) throws IOException {
		URL url = new URL(fileURL);
		httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		// Always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String disposition = httpConn.getHeaderField("Content-Disposition");
			String contentType = httpConn.getContentType();
			contentLength = httpConn.getContentLength();

			if (disposition != null) {
				// Extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10, disposition.length() - 1);
				}
			} else {
				// Extracts file name from URL
				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
			}

			// Output for debugging purpose only
			Launcher.logModule.log(Case.INFO, "Content-Type = " + contentType);
			Launcher.logModule.log(Case.INFO, "Content-Disposition = " + disposition);
			Launcher.logModule.log(Case.INFO, "Content-Length = " + contentLength);
			Launcher.logModule.log(Case.INFO, "fileName = " + fileName);

			// Opens input stream from the HTTP connection
			inputStream = httpConn.getInputStream();

		} else {
			throw new IOException("No file to download. Server replied HTTP code: " + responseCode);
		}
	}

	public void disconnect() throws IOException {
		inputStream.close();
		httpConn.disconnect();
	}

	public String getFileName() {
		return this.fileName;
	}

	public int getContentLength() {
		return this.contentLength;
	}

	public InputStream getInputStream() {
		return this.inputStream;
	}
}
