package br.com.centraldaassinatura.loja.infra;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

import javax.servlet.http.Part;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.com.centraldaassinatura.loja.model.Company;

public class FileSaver {

	public String write(Part mainImage, String imagesUploadedPathWithId) {
		String fullPath = this.getClass().getClassLoader().getResource("").getPath();
		String serverPath = getServerPath(fullPath);
		File file = new File(serverPath + imagesUploadedPathWithId);
		if (!file.exists()) {
			file.mkdir();
		}
		String relativePath = imagesUploadedPathWithId + "/" + mainImage.getSubmittedFileName();
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
			// try() close resource automatically
			try (ReadableByteChannel inputChannel = Channels.newChannel(input)) {
				WritableByteChannel outputChannel = Channels.newChannel(outputStream);
				ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 10);
				while (inputChannel.read(buffer) != -1) {
					buffer.flip();// reset buffer
					outputChannel.write(buffer);
					buffer.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fileUploadEvent(FileUploadEvent event, Company company) {
		try {
			String fullPath = this.getClass().getClassLoader().getResource("").getPath();
			UploadedFile arq = event.getFile();
			InputStream in = new BufferedInputStream(arq.getInputstream());
			System.out.println(
					"Salvou: " + FileSaver.getServerPath(fullPath) + "imagesUploaded/companyId" + company.getId());
			File file = new File(FileSaver.getServerPath(fullPath) + "imagesUploaded/companyId" + company.getId());
			if (!file.exists()) {
				file.mkdir();
			}
			FileOutputStream fout = new FileOutputStream(file + "/" + arq.getFileName());
			while (in.available() != 0) {
				fout.write(in.read());
				fout.flush();
			}
			fout.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
