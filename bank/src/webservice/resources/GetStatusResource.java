package webservice.resources;

import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class GetStatusResource extends ServerResource {

	@Post
	public String getStatus(Representation entity) {
		Form request = new Form(this.getRequestEntity());

		JSONObject info = JSONObject.fromObject(request.getValues("info"));
		// TODO - call the algorithm and return a json {code:1, status:Aprobat}
		
		return "";
	}
}
