package webservice.resources;

import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import database.DBOperations;

public class UpdateUserAccountResource extends ServerResource {

	@Post
	public void updateAccount(Representation entity) {
		Form request = new Form(this.getRequestEntity());

		JSONObject info = JSONObject.fromObject(request.getValues("info"));
		int userId = info.getInt("userId");
		float amount = (float)info.getDouble("amount");
		boolean isWithdrawal = info.getInt("isWithdrawal") == 1;
		
		DBOperations.updateAccount(userId, amount, isWithdrawal);
	}
}

/*

{info: {
			userId: 1,
			amount: 12.5,
			isWthdrawal: 1,
		}
}

*/