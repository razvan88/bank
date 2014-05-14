package webservice.application;

import org.restlet.*;
import org.restlet.data.Protocol;

/**
 * This class starts and stops the webservice
 */
public class WebserviceApp {
	private static Component component;
	private static WebserviceApp webservice;
	
	static {
		component = new Component();
		webservice = new WebserviceApp();
	}
	
	private WebserviceApp() { }
	
	public static WebserviceApp getInstance() {
		return webservice;
	}
	
	public void startWebservice() throws Exception {
		if(component.isStarted())
			return;
		
		int httpPort = 31889;
		final String maxThreadsKey = "maxThreads";
		final String maxThreadsVal = "512";
		
		Server server = component.getServers().add(Protocol.HTTP, httpPort);  
		server.getContext().getParameters().add(maxThreadsKey, maxThreadsVal); 
		component.getDefaultHost().attach(new WebserviceDispatcher());  
		component.start();
	}
	
	public void stopWebservice() throws Exception {
		if (component.isStopped())
			return;
		
		component.stop();
	}
}
