package br.com.centraldaassinatura.loja.servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/file/*")
public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = -8930520538841086249L;

	protected void service(HttpServletRequest req, HttpServletResponse res) {
		try {
			String path = req.getRequestURI().split("/file")[1];
			System.out.println("Path &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& " + path);
			Path source = Paths.get(path.substring(1, path.length()));
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String contentType = fileNameMap.getContentTypeFor("file:" + source);
			res.reset();
			res.setContentType(contentType);
			res.setHeader("Content-Length", String.valueOf(Files.size(source)));
			res.setHeader("Content-Disposition", "filename=\""+source.getFileName().toString() + "\"");
			
			FileInputStream input = new FileInputStream(source.toFile());
			try(ReadableByteChannel inputChannel = Channels.newChannel(input)){
				WritableByteChannel outputChannel = Channels.newChannel(res.getOutputStream());
				ByteBuffer buffer = ByteBuffer.allocateDirect(1024*10);
				while(inputChannel.read(buffer) != -1){
					buffer.flip();
					outputChannel.write(buffer);
					buffer.clear();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
