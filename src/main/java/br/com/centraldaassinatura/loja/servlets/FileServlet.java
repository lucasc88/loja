package br.com.centraldaassinatura.loja.servlets;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.centraldaassinatura.loja.infra.FileSaver;

@WebServlet("/file/*")
public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = -8930520538841086249L;

	protected void service(HttpServletRequest req, HttpServletResponse res) {
		try {
//			req.getRequestURI().split("/file")[0] contains localhost:8080... and
//			o req.getRequestURI().split("/file")[1] contains relativePath from image
			String relativePath = req.getRequestURI().split("/file")[1];
			
			//get path server with relativePath
			String fullPath = this.getClass().getClassLoader().getResource("").getPath();
			Path source = Paths.get(FileSaver.getServerPath(fullPath) + relativePath);
			
			//get from OS
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String contentType = fileNameMap.getContentTypeFor("file:" + source);
			
			//settings for the browser rendering images
			res.reset();
			res.setContentType(contentType);
			res.setHeader("Content-Length", String.valueOf(Files.size(source)));
			res.setHeader("Content-Disposition", "filename=\"" + source.getFileName().toString() + "\"");

			FileSaver.transfer(source, res.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
