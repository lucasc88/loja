package br.com.centraldaassinatura.loja.infra;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

import javax.servlet.http.Part;

public class FileSaver {

	public String write(Part mainImage, String path) {
		String fullPath = this.getClass().getClassLoader().getResource("").getPath();
		String serverPath = getServerPath(fullPath);
		String relativePath = path + "/" + mainImage.getSubmittedFileName();
		try {
			// save in the disk
			mainImage.write(serverPath + relativePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return relativePath;
	}

	public static String getServerPath(String fullPath) {
		int index = fullPath.lastIndexOf("0.Final/");
		String serverPath = fullPath.substring(0, index + "0.Final/".length());
		String osAppropriatePath = System.getProperty("os.name").contains("indow") ? serverPath.substring(1)
				: serverPath;
		return osAppropriatePath;
	}

	public static void transfer(Path source, OutputStream outputStream) {
		FileInputStream input;
		try {
			input = new FileInputStream(source.toFile());
			//try() close resource automatically
			try (ReadableByteChannel inputChannel = Channels.newChannel(input)) {
				WritableByteChannel outputChannel = Channels.newChannel(outputStream);
				ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 10);
				while (inputChannel.read(buffer) != -1) {
					buffer.flip();//reset buffer
					outputChannel.write(buffer);
					buffer.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
