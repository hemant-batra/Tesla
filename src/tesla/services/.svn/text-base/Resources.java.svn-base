package tesla.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tesla.core.ServerManager;
import tesla.core.Service;
import tesla.store.Request;
import tesla.store.ServerBean;

public class Resources extends Service<String> {

	private Map<String,String> parameters = null; 
	
	public Resources(ServerBean serverBean) {
		super(serverBean);
	}

	public void setResources(List<String> pages, List<String> images, List<String> services, String applicationName) 
	{
		String pagesCSV = "";
		for(String name : pages) {
			pagesCSV += name + ",";
		}
		
		String imagesCSV = "";
		for(String name : images) {
			imagesCSV += name + ",";
		}
		
		String servicesCSV = "";
		for(String name : services) {
			name = name.substring(0, name.length()-6);
			servicesCSV += name + ",";
		}
		
		parameters = new HashMap<String, String>();
		parameters.put("applicationName", applicationName);
		parameters.put("pages", pagesCSV);
		parameters.put("images", imagesCSV);
		parameters.put("services", servicesCSV);
	}
	
	@Override
	protected String execute(Request request) {
		parameters.put("contextName", request.getServerBean().getClass().getSimpleName());
		return ServerManager.getPageContent(request.getServerBean().getPort(), "teslaResources.html", parameters);
	}

	@Override
	protected boolean isPasswordProtected() {
		return true;
	}

}