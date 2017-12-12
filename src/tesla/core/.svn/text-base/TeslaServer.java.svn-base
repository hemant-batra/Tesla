package tesla.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import tesla.exception.TeslaIOException;
import tesla.exception.TeslaServerInitializationException;
import tesla.services.Documentation;
import tesla.services.Resources;
import tesla.services.RootContext;
import tesla.services.Shutdown;
import tesla.store.FileBuffer;
import tesla.store.FileBuffer.ContentType;
import tesla.store.ServerBean;

import com.sun.net.httpserver.HttpServer;

public class TeslaServer implements Runnable {
	
	private ServerBean serverBean = null;
	private HttpServer httpServer = null;
	private Map<String,FileBuffer> mapFileBuffer = null;
	private PrintStream printStream = null;
	
	TeslaServer(ServerBean serverBean) throws TeslaServerInitializationException, TeslaIOException 
	{
		if(serverBean==null)
			throw new TeslaServerInitializationException("Server Bean object cannot be null.");
		this.serverBean = serverBean;
		
		if(serverBean.getConsoleOutputFileName()!=null) {
			try {
				printStream = new PrintStream(new File(serverBean.getConsoleOutputFileName()));
				System.setOut(printStream);
				System.setErr(printStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		mapFileBuffer = new HashMap<String, FileBuffer>();
		
		try 
		{
			List<String> pages = getClassNamesFromPackage(serverBean.getPagesPackageName());
			List<String> images = getClassNamesFromPackage(serverBean.getImagesPackageName());
			List<String> services = getClassNamesFromPackage(serverBean.getServicesPackageName());
			
			httpServer = HttpServer.create(new InetSocketAddress(serverBean.getPort()), 0);
			
			String applicationName = serverBean.getApplicationName(); 
			if(applicationName==null || applicationName.isEmpty())
				applicationName = serverBean.getClass().getSimpleName();
			
			String internalServices = "/Documentation, /Resources, /Shutdown or /Logo";
			if(internalServices.contains(serverBean.getClass().getSimpleName())) {
				throw new TeslaServerInitializationException("Server Bean name cannot be same as any of the internal services: "+internalServices);
			}
			
			System.out.println("Starting "+applicationName);
			System.out.println("Loading root context [/"+serverBean.getClass().getSimpleName()+"]");
			addPage("teslaStart.html",new FileBuffer(this.getClass(), "tesla/pages/teslaStart.html", ContentType.TEXT));
			if(serverBean.getStartupServiceName()==null)
				httpServer.createContext("/"+serverBean.getClass().getSimpleName(), new RootContext(serverBean)).getFilters().add(new RequestFilter());
			else
			{
				String contextName = "/"+serverBean.getClass().getSimpleName();
				@SuppressWarnings("unchecked")
				Class<Service<? extends Serializable>> classService = (Class<Service<? extends Serializable>>) Class.forName(serverBean.getServicesPackageName()+"."+serverBean.getStartupServiceName());
				Constructor<Service<? extends Serializable>> constructor = classService.getConstructor(new Class[] { ServerBean.class });
				Service<? extends Serializable> service = (Service<? extends Serializable>) constructor.newInstance(new Object[] { serverBean });
				httpServer.createContext(contextName,service).getFilters().add(new RequestFilter());
			}
			httpServer.createContext("/"+serverBean.getClass().getSimpleName()+"/pages/teslaIndex.html", new FileBuffer(this.getClass(),"tesla/pages/teslaIndex.html",ContentType.TEXT));
			
			System.out.println("Loading internal service [/Documentation]");
			addPage("teslaDocumentation.html",new FileBuffer(this.getClass(), "tesla/pages/teslaDocumentation.html", ContentType.TEXT));
			httpServer.createContext("/"+Documentation.class.getSimpleName(), new Documentation(serverBean)).getFilters().add(new RequestFilter());
			
			System.out.println("Loading internal service [/Resources]");
			addPage("teslaResources.html",new FileBuffer(this.getClass(), "tesla/pages/teslaResources.html", ContentType.TEXT));
			Resources resources = new Resources(serverBean);
			resources.setResources(pages, images, services, applicationName);
			httpServer.createContext("/"+Resources.class.getSimpleName(), resources).getFilters().add(new RequestFilter());
			
			System.out.println("Loading internal service [/Shutdown]");
			httpServer.createContext("/"+Shutdown.class.getSimpleName(), new Shutdown(serverBean)).getFilters().add(new RequestFilter());
			
			System.out.print("Loading internal service [/Logo]");
			httpServer.createContext("/Logo", new FileBuffer(this.getClass(),"tesla/images/tesla-server.png",ContentType.IMAGE));
			
			String contextName = null;
			lnprint("Loading Pages...");
			if(pages.size()>0) {
			for(String name : pages) {
				contextName = "/"+serverBean.getClass().getSimpleName()+"/pages/"+name;
				FileBuffer pageBuffer = new FileBuffer(serverBean.getClass(), serverBean.getPagesPackageName().replaceAll(Pattern.quote("."),"/")+"/"+name, ContentType.TEXT);
				addPage(name, pageBuffer);
				httpServer.createContext(contextName,pageBuffer);
				printContextName(contextName);
			}
			} else System.out.print("No pages found");
			
			lnprint("Loading Images...");
			if(images.size()>0) {
			for(String name : images) {
				contextName = "/"+serverBean.getClass().getSimpleName()+"/images/"+name;
				httpServer.createContext(contextName,new FileBuffer(serverBean.getClass(), serverBean.getImagesPackageName().replaceAll(Pattern.quote("."),"/")+"/"+name, ContentType.IMAGE));
				printContextName(contextName);
			}
			} else System.out.print("No images found");
			
			lnprint("Loading Services...");
			if(services.size()>0) {
			for(String name : services) {
				name = name.split(Pattern.quote("."))[0];
				contextName = "/"+serverBean.getClass().getSimpleName()+"/services/"+name;
				@SuppressWarnings("unchecked")
				Class<Service<? extends Serializable>> classService = (Class<Service<? extends Serializable>>) Class.forName(serverBean.getServicesPackageName()+"."+name);
				Constructor<Service<? extends Serializable>> constructor = classService.getConstructor(new Class[] { ServerBean.class });
				Service<? extends Serializable> service = (Service<? extends Serializable>) constructor.newInstance(new Object[] { serverBean });
				httpServer.createContext(contextName,service).getFilters().add(new RequestFilter());
				printContextName(contextName);
			}
			} else System.out.print("No services found");
			
			httpServer.setExecutor(null);
		} catch (Exception e) {
			stop();
			e.printStackTrace();
			throw new TeslaServerInitializationException(e);
		} 
	}
	
	public void run() {
		httpServer.start();
		System.out.println("\nServer started on port "+serverBean.getPort());
	}
	
	private List<String> getClassNamesFromPackage(String packageName) throws IOException, URISyntaxException 
	{
	    List<String> names = new ArrayList<String>();
	    if(packageName==null || packageName.isEmpty())
	    	return names;
	    
	    if(serverBean.getClass().getClassLoader().getResource(packageName)==null)
	    {
	    	CodeSource src = serverBean.getClass().getProtectionDomain().getCodeSource();
	    	if (src != null) {
	    	  ZipInputStream zipInputStream = new ZipInputStream(src.getLocation().openStream());
	    	  while(true) {
	    	    ZipEntry zipEntry = zipInputStream.getNextEntry();
	    	    if (zipEntry == null)
	    	      break;
	    	    String name = zipEntry.getName();
	    	    if(name.startsWith(packageName)) {
	    	    	name = name.replaceAll(Pattern.quote(packageName+"/"), "");
	    	    	names.add(name);
	    	    }
	    	  }
	    	}	    	
	    } else {
	    	packageName = packageName.replace(".", "/");
	    	URL packageURL = serverBean.getClass().getClassLoader().getResource(packageName);
	    	URI uri = new URI(packageURL.toString());
	    	File folder = new File(uri.getPath());
	        File[] contenuti = folder.listFiles();
	        for(File actual: contenuti){
	            names.add(actual.getName());
	        }
	    }
	    return names;
	}

	void stop() {
		if(printStream!=null) 
		{
			printStream.close();
			System.setOut(null);
			System.setErr(null);
		}
		
		if(httpServer!=null)
			httpServer.stop(0);
	}
	
	ServerBean getServerBean() {
		return serverBean;
	}
	
	private void addPage(String fileName, FileBuffer fileBuffer) {
		mapFileBuffer.put(fileName, fileBuffer);
	}
	
	String getPageContent(String fileName) {
		return mapFileBuffer.get(fileName).getContent();
	}
	
	private void lnprint(String message) {
		System.out.print("\n"+message);
	}
	
	private void printContextName(String contextName) {
		lnprint("["+contextName+"]");
	}
}