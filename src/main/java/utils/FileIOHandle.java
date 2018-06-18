package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//import com.google.gson.Gson;

public class FileIOHandle {

	public static void writeTextToFile(String text, String fileName,
			String directoryName) {

		File directory = new File(directoryName);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		File file = new File(directoryName + "/" + fileName);
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void appendTextToFile(String text, String fileName,
			String directoryName) {

		File directory = new File(directoryName);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		File file = new File(directoryName + "/" + fileName);
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
