package tesla.services;

import java.util.HashMap;
import java.util.Map;

import tesla.core.ServerManager;
import tesla.core.Service;
import tesla.store.Request;
import tesla.store.ServerBean;

public class RootContext extends Service<String> {

	public RootContext(ServerBean serverBean) {
		super(serverBean);
	}

	@Override
	protected String execute(Request request) {
			Map<String,String> parameters = new HashMap<String, String>();
			parameters.put("contextName", request.getServerBean().getClass().getSimpleName());
			return ServerManager.getPageContent(request.getServerBean().getPort(),"teslaStart.html",parameters);
	}

	@Override
	protected boolean isPasswordProtected() {
		return false;
	}
}