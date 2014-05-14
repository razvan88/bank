package webservice.resources;

import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import database.DBOperations;

public class ChangeAccountLockStatusResource extends ServerResource {

	@Post
	public void changeAccountStatus(Representation entity) {
		Form request = new Form(this.getRequestEntity());

		JSONObject info = JSONObject.fromObject(request.getValues("info"));
		int userId = info.getInt("userId");
		int lock = info.getInt("lock");
		
		DBOperations.changeAccountLockStatus(userId, lock);
	}
}

/*

{info: {
			userId: 1,
			lock: 0
		}
}

*/