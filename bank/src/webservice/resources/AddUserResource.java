package webservice.resources;

import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import database.DBOperations;

public class AddUserResource extends ServerResource {

	@Post
	public String addUser(Representation entity) {
		Form request = new Form(this.getRequestEntity());

		JSONObject info = JSONObject.fromObject(request.getValues("info"));
		DBOperations.addUser(info);
		
		return new JSONObject().toString();
	}
}

/*

{info: {
 			nume: Aaa
 			prenume: Bbb
 			cnp: 123
 			domeniu: 1
 		}
}

 */