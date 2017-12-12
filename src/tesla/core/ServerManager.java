package tesla.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import tesla.exception.TeslaIOException;
import tesla.exception.TeslaServerInitializationException;
import tesla.store.ServerBean;

public class ServerManager {
	
	private static Map<Integer,TeslaServer> servers = new HashMap<Integer, TeslaServer>();
	
	public static void start(ServerBean serverBean) throws TeslaServerInitializationException, TeslaIOException 
	{
		TeslaServer server = servers.get(serverBean.getPort());
		if(server==null)
		{
			server = new TeslaServer(serverBean);
			servers.put(serverBean.getPort(), server);
			new Thread(server).start();
		}
		else throw new TeslaServerInitializationException("Server is already running on port "+serverBean.getPort());
	}
	
	public static boolean isRunning(int port) {
		return servers.containsKey(port);
	}
	
	public static ServerBean getServerBean(int port) {
		TeslaServer server = servers.get(port);
		if(server==null)
			return null;
		return server.getServerBean();
	}
	
	public static int[] getActivePorts() {
		List<Integer> list = new ArrayList<Integer>();
		Iterator<Integer> iterator = servers.keySet().iterator();
		while(iterator.hasNext()) {
			list.add(iterator.next());
		}
		int count = 0;
		int[] returnArray = new int[list.size()];
		for(int i : list) {
			returnArray[count++] = i;
		}
		return returnArray;
	}
	
	public static boolean shutdown(int port) {
		TeslaServer teslaServer = servers.get(port);
		if(teslaServer==null)
			return false;
		else {
			teslaServer.stop();
			servers.remove(port);
			return true;
		}
	}
	
	public static String getPageContent(int port, String pageName) {
		return servers.get(port).getPageContent(pageName);
	}
	
	public static String getPageContent(int port, String pageName, Map<String,String> parameters) {
		String content = servers.get(port).getPageContent(pageName);
		Iterator<String> iterator = parameters.keySet().iterator();
		while(iterator.hasNext())
		{
			String key = iterator.next();
			try {
				String value = parameters.get(key);
				if(value==null)
					parameters.put(key, "");
				if(parameters.get(key).indexOf("\\")!=-1)
					parameters.put(key,parameters.get(key).replaceAll(Pattern.quote("\\"), "/"));
				content = content.replaceAll(Pattern.quote("{"+key+"}"), parameters.get(key));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return content;
	}
}