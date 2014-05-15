package webservice.resources;

import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import database.DBOperations;

public class UpdateUserInfoResource extends ServerResource {

	@Post
	public String updateUser(Representation entity) {
		Form request = new Form(this.getRequestEntity());

		JSONObject info = JSONObject.fromObject(request.getValues("info"));
		DBOperations.updateUserInfo(info);
		
		return new JSONObject().toString();
	}
}

/*

{info: {
			nume: Aaa
			prenme: Bbb
			cnp: 123
			domeniu: 1
		}
}

*/