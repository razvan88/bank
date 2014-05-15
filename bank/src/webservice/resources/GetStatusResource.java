package webservice.resources;

import loan.Algorithm;
import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import database.DBOperations;

public class GetStatusResource extends ServerResource {

	@Post
	public String getStatus(Representation entity) {
		Form request = new Form(this.getRequestEntity());

		JSONObject info = JSONObject.fromObject(request.getValues("info"));
		
		Algorithm alg = new Algorithm(info);
		int code = alg.computeStatus();
		String status = DBOperations.getStatus(code);
		
		JSONObject statusObj = new JSONObject();
		statusObj.put("status", status);
		
		return statusObj.toString();
	}
}

/* 

{
	info: {
				alteCredite: 1,
				sumaAlteRate: 123.56,
				venitLC: 342.23,
				venitLC_1: 234.56,
				venitLC_2: 435.12,
				bonus3M: 234.12,
				venitAnAnterior: 34562.456,
				expId: 1,
				domId: 3,
				sumaCreditata: 234544.23,
				nrRate: 12,
				dae: 23.45
		}
}

*/