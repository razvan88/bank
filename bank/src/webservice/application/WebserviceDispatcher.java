package webservice.application;


import org.restlet.*;
import org.restlet.routing.Router;

import webservice.resources.AddUserResource;
import webservice.resources.ChangeAccountLockStatusResource;
import webservice.resources.GetDomainsResource;
import webservice.resources.GetExperienceResource;
import webservice.resources.GetStatusResource;
import webservice.resources.GetUserInfoResource;
import webservice.resources.UpdateUserInfoResource;
import webservice.resources.UpdateUserAccountResource;
import webservice.resources.UpdateUserLoanResource;


/**
 * Used to create a root restlet that will receive all the
 * incoming requests
 */
public class WebserviceDispatcher extends Application {
	
	@Override
	public synchronized Restlet createInboundRoot() {
		Router router = new Router(getContext());
		
		router.attach("/getUserInfo", GetUserInfoResource.class);
		router.attach("/addUser", AddUserResource.class);
		router.attach("/updateUserInfo", UpdateUserInfoResource.class);
		router.attach("/updateUserLoan", UpdateUserLoanResource.class);
		router.attach("/updateUserAccount", UpdateUserAccountResource.class);
		router.attach("/setAccountLock", ChangeAccountLockStatusResource.class);
		router.attach("/getDomains", GetDomainsResource.class);
		router.attach("/getExperience", GetExperienceResource.class);
		router.attach("/getStatus", GetStatusResource.class);
		
		return router;
	}
}
