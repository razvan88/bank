package webservice.resources;

import net.sf.json.JSONArray;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import database.DBOperations;

public class GetExperienceResource extends ServerResource {

	@Post
	public String getExperienceList(Representation entity) {
		JSONArray experience = DBOperations.getExperience();
		return experience.toString();
	}
}
