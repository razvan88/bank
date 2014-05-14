package webservice.resources;

import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import database.DBOperations;

public class UpdateUserLoanResource extends ServerResource {

	@Post
	public void updateLoan(Representation entity) {
		Form request = new Form(this.getRequestEntity());

		JSONObject info = JSONObject.fromObject(request.getValues("info"));
		int userId = info.getInt("userId");
		String loan = info.getString("loan");
		
		DBOperations.updateLoan(userId, loan);
	}
}

/*

{info: {
			userId: 1
			loan: ...StringJSON...
		}
}

 */
