package webservice.resources;

import net.sf.json.JSONArray;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import database.DBOperations;

public class GetDomainsResource extends ServerResource {

	@Post
	public String getDomainList(Representation entity) {
		JSONArray domains = DBOperations.getDomanins();
		return domains.toString();
	}
}
