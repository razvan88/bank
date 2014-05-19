package webservice.resources;

import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import database.DBOperations;
import loan.Algorithm;

public class GetNewLoanResource extends ServerResource {
	
	@Post
	public String setNewLoan(Representation entity) {
		Form request = new Form(this.getRequestEntity());

		JSONObject info = JSONObject.fromObject(request.getValues("info"));
		int userId = info.getInt("userId");
		float credit = (float)info.getDouble("credit");
		float dae = (float)info.getDouble("dae");
		int nrRate = info.getInt("nrRate");
		
		JSONObject newLoan = Algorithm.createLoanRates(credit,dae, nrRate);
		String loan = newLoan.toString();
		
		DBOperations.createNewLoan(userId, loan);
		
		return loan;
	}
}
