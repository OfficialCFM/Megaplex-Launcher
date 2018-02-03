package officialcfm.launcher;

import java.io.IOException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Uses Zip4j to unzip a zip file
 * 
 * @author KP
 */
public class UnzipUtility {
	public void unzip(String zipFilePath, String destDirectory) throws IOException {
		try {
			ZipFile file = new ZipFile(zipFilePath);
			file.extractAll(destDirectory);
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}
}
