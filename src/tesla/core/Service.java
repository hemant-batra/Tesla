package tesla.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import tesla.store.Request;
import tesla.store.ServerBean;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class Service<ReturnType extends Serializable> implements HttpHandler 
{
	protected abstract ReturnType execute(Request request);
	protected abstract boolean isPasswordProtected();
	
	private ServerBean serverBean = null;
	public Service(ServerBean serverBean) {
		this.serverBean = serverBean;
	}
	
	public void handle(HttpExchange httpExchange) throws IOException 
	{
		@SuppressWarnings("unchecked")
		Map<String, String> params = (Map<String, String>)httpExchange.getAttribute("parameters");
		InputStream inputStream = httpExchange.getRequestBody();
		
		Serializable object = null;
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			object = (Serializable) objectInputStream.readObject();
		} catch (Exception e) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			StringBuilder builder = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					builder.append("\n");
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException ioe2) {
					ioe2.printStackTrace();
				}
			}
			object = builder.toString();
		}
		
		Headers headers = httpExchange.getResponseHeaders();
		headers.add("Content-Type","text/html; charset=UTF-8");
		
		byte[] bytes = null;
		ReturnType response = null;
		if(isPasswordProtected() && serverBean.getPassword()!=null)
		{
				String password = (String) params.get("password");
				if(password==null || !password.equals(serverBean.getPassword()))
						bytes = "Authentication Failed".getBytes();
				else
				{
					String requestIP = httpExchange.getRemoteAddress().getAddress().getHostAddress();
					Request request = new Request(requestIP,object,params,serverBean);
					response = execute(request);
					if(response==null)
						bytes = "".getBytes();
				}
		}
		else
		{
			String requestIP = httpExchange.getRemoteAddress().getAddress().getHostAddress();
			Request request = new Request(requestIP,object,params,serverBean);
			response = execute(request);
			if(response==null)
				bytes = "".getBytes();	
		}
		
		if(bytes == null)
		{
			bytes = (response instanceof String)?((String) response).getBytes():serialize(response);
		}
		
		httpExchange.sendResponseHeaders(200, bytes.length);
		OutputStream outputStream = httpExchange.getResponseBody();
		outputStream.write(bytes);
		outputStream.close();		
	}
	
	private byte[] serialize(ReturnType response) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		try {
			 objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			 objectOutputStream.writeObject(response);
			 return byteArrayOutputStream.toByteArray();
	      } catch(Exception e)  {
	          return e.getMessage().getBytes();
	      } finally {
	    	 try {
				if(objectOutputStream!=null) objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	      }
	}
	
	protected final String getPageContent(String pageName) {
		return ServerManager.getPageContent(serverBean.getPort(), pageName);
	}
	
	protected final String getPageContent(String pageName, Map<String,String> parameterMap) {
		return ServerManager.getPageContent(serverBean.getPort(), pageName, parameterMap);
	}
	
}