package webservice.resources;

import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import database.DBOperations;

public class GetUserInfoResource extends ServerResource {

	@Post
	public String getUser(Representation entity) {
		Form request = new Form(this.getRequestEntity());

		JSONObject info = JSONObject.fromObject(request.getValues("info"));
		String cnp = info.getString("cnp");
		
		JSONObject userInfo = DBOperations.getUserByCnp(cnp);
		return userInfo.toString();
	}
}

/*
 
{info: {
			cnp: 123
		}
}
  
 */
