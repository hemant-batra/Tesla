package tesla.services;

import tesla.core.ServerManager;
import tesla.core.Service;
import tesla.store.Request;
import tesla.store.ServerBean;

public class Documentation extends Service<String> {

	public Documentation(ServerBean serverBean) {
		super(serverBean);
	}

	@Override
	protected String execute(Request request) {
		return ServerManager.getPageContent(request.getServerBean().getPort(), "teslaDocumentation.html");
	}

	@Override
	protected boolean isPasswordProtected() {
		return false;
	}

}