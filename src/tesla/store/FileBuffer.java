package tesla.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import sun.misc.IOUtils;
import tesla.exception.TeslaIOException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class FileBuffer implements HttpHandler {

	public static enum ContentType {TEXT, IMAGE}
	private byte[] content;
	private String contentType;
	
	public FileBuffer(Class<?> invokerClass, String fileName, ContentType contentType) throws TeslaIOException {
		InputStream inputStream = invokerClass.getClassLoader().getResourceAsStream(fileName);
		try {
			content = IOUtils.readFully(inputStream,-1,true);
		} catch (IOException e) {
			throw new TeslaIOException(e);
		}
		
		String fileExtension = fileName.split(Pattern.quote("."))[1].toLowerCase();
		if(contentType==ContentType.TEXT)
			this.contentType = "text/"+fileExtension+"; charset=UTF-8";
		else if(contentType==ContentType.IMAGE)
			this.contentType = "image/"+fileExtension;
	}
	
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		Headers headers = httpExchange.getResponseHeaders();
		headers.add("Content-Type", contentType);
		httpExchange.sendResponseHeaders(200, content.length);
		OutputStream outputStream = httpExchange.getResponseBody();
		outputStream.write(content);
		outputStream.close();
	}
	
	public String getContent() {
		return new String(content);
	}
}