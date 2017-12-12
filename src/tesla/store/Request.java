package tesla.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tesla.exception.TeslaDeserializationException;
import tesla.exception.TeslaSerializationException;

public class Request {

	private String requestIP;
	private Serializable requestObject;
	private ServerBean serverBean;
	private Map<String, String> attributes;
	
	public Request(String requestIP, Serializable requestObject, Map<String, String> attributes, ServerBean serverBean) {
		this.requestIP = requestIP;
		this.requestObject = requestObject;
		this.serverBean = serverBean;
		this.attributes = attributes;
	}
	public String getRequestIP() {
		return requestIP;
	}
	public Serializable getRequestObject() {
		return requestObject;
	}
	public ServerBean getServerBean() {
		return serverBean;
	}
	public String getAttribute(String attributeKey) {
		return attributes.get(attributeKey);
	}
	public List<String> listAttributeKeys() {
		List<String> list = new ArrayList<String>();
		Iterator<String> iterator = attributes.keySet().iterator();
		while(iterator.hasNext())
			list.add(iterator.next());
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getParameterMap() throws TeslaSerializationException, TeslaDeserializationException  {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = null;
			try {
				 objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
				 objectOutputStream.writeObject(attributes);
		      } catch(Exception e)  {
		          throw new TeslaSerializationException(e);
		      } finally {
		    	 try {
					if(objectOutputStream!=null) objectOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		      }
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			ObjectInputStream objectInputStream = null;
			try {
				objectInputStream = new ObjectInputStream(byteArrayInputStream);
				return (Map<String, String>) objectInputStream.readObject(); 
			} catch(Exception e) {
				throw new TeslaDeserializationException(e);
			} finally {
				try {
					if(objectInputStream!=null) objectInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
	
	public byte[] forward(String url) throws UnsupportedEncodingException {
		if(url==null)
			return null;
		
		String appender = url.contains("?")?"&":"?";
		for(String key : listAttributeKeys()) {
			appender += key + "=" + URLEncoder.encode(attributes.get(key),"UTF-8");
		}
		
		url += appender;
		
		return hitURL(url, requestObject);
	}
	
	public static byte[] hitURL(String url, Serializable data) 
	{
		OutputStream output = null;
		InputStream response = null;
		ByteArrayOutputStream buffer = null;
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

			if(data!=null) {
				output = connection.getOutputStream();
				output.write(serialize(data));
			}

			response = connection.getInputStream();
			buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytes = new byte[16384];

			while ((nRead = response.read(bytes, 0, bytes.length)) != -1) {
			  buffer.write(bytes, 0, nRead);
			}

			return buffer.toByteArray();
		} catch (Exception e) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			return stringWriter.toString().getBytes();
		} finally {
			try {
				if(buffer!=null) buffer.flush();
				if(response!=null) response.close();
				if(output!=null) output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static byte[] serialize(Serializable data) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		try {
			 objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			 objectOutputStream.writeObject(data);
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
}