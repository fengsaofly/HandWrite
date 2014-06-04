package scu.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipUtils {
	private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

	public static void zipFiles(File file, File zipFile) {
		try {
			zipFile.createNewFile();
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(zipFile), BUFF_SIZE));
			zipFile(file, out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void zipFile(File file, ZipOutputStream out) {
		String path = file.getName();

		byte[] buf = new byte[BUFF_SIZE];

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File aFile : files) {
				zipFile(aFile, out);
			}
		} else {
			try {
				BufferedInputStream in = new BufferedInputStream(
						new FileInputStream(file), BUFF_SIZE);
				out.putNextEntry(new ZipEntry(path));
				int length;
				while ((length = in.read(buf)) != -1) {
					out.write(buf, 0, length);
				}
				in.close();
				out.flush();
				out.closeEntry();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static LinkedList<File> traverseDir(String dirPath) {
		LinkedList<File> files = new LinkedList<File>();
		File dir = new File(dirPath);
		File[] file = dir.listFiles();
		for (File aFile : file) {
			if (aFile.isDirectory()) {
				files.add(aFile);
			}
		}
		for (File aFile : files) {
			files.removeFirst();
			if (aFile.isDirectory()) {
				file = aFile.listFiles();
				for (File tFile : file) {
					files.add(tFile);
				}
			}
		}
		return files;
	}

}
