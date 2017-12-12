package tesla.services;

import tesla.core.ServerManager;
import tesla.core.Service;
import tesla.store.Request;
import tesla.store.ServerBean;

public class Shutdown extends Service<String> implements Runnable {
	
	private int port;
	private Thread shutDownThread;
	public Shutdown(ServerBean serverBean) {
		super(serverBean);
		port = serverBean.getPort();
		shutDownThread = new Thread(this);
	}

	@Override
	protected String execute(Request request) {
		shutDownThread.start();
		String applicationName = request.getServerBean().getApplicationName(); 
		if(applicationName==null || applicationName.isEmpty())
			applicationName = request.getServerBean().getClass().getSimpleName();
		return applicationName + " on port " + port + " has been shutdown\n";
	}
	
	@Override
	protected boolean isPasswordProtected() {
		return true;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ServerManager.shutdown(port);
	}
}