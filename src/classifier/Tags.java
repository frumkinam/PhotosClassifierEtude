package classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Tags extends ArrayList<Tag> {
	PhotosDatabase db;
	String userIdString;
	ArrayList<Hashtable<String, String>> photos;
	
public	Tags (PhotosDatabase db, String userIdString) {
	    clear(); 
		this.db = db;
		this.userIdString=userIdString;
		db.createTable("Tags","userid","integer","name","text","description","text");
		} // End of constructor

public void sampleForMarking() {
	String sql = String.format("SELECT id, name, description from Tags WHERE userid=%s ORDER BY name", userIdString);
	// In future the only statement must convert sql to Tags !!!
	ArrayList<Hashtable<String, String>> tags = db.SqlToHashTableList(sql, "id", "name","description");
	// System.out.println(tags);
	clear();
	for (int m=0; m<tags.size(); m++) { 
		Tag tag=new Tag();
		tag.setFields(tags.get(m).get("id"),tags.get(m).get("name"),tags.get(m).get("description"));
		add(tag);
	}
} // End of sampleForMarking

	public String jsonSampleForMarking() throws JsonProcessingException {
		sampleForMarking();
		ObjectMapper om = new ObjectMapper();
		ArrayList<String> array = new ArrayList<String>();
		for (int m = 0; m < size(); m++)
			array.add(om.writeValueAsString(get(m)));
		return Utils.jsonListIntoBrackets(array);
	}

	public void sampleFromMarking(String jsonArray) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		ArrayList<Tag> array = om.readValue(jsonArray, new TypeReference<ArrayList<Tag>>() {
		});
		clear();
		addAll(array);
	} // End of sampleFromMarking
	
	
	public Tag retriveAnalog(Tag tag) {
		for (int m = 0; m < size(); m++) {
			Tag currentTag = get(m);
			if (currentTag.getId().compareTo(tag.getId()) == 0) {
				remove(m);
				show("in retrive");
				return currentTag;
			}
		}
		return null;
	} // End of retriveAnalog
	
	public void handleMarking(String jsonArray) throws JsonParseException, JsonMappingException, IOException {
		sampleForMarking();
		// show("handleMarkingBeforeDeleting");
		Tags newTags = new Tags(db, userIdString);
		newTags.sampleFromMarking(jsonArray);
		newTags.show("newTagsBeforeDeleting");
		for (int m = 0; m < size(); m++) {
			Tag currentTag = get(m);
			Tag analog = newTags.retriveAnalog(currentTag);
			if (analog == null)
				currentTag.deleteWithRelation(db, userIdString);
			else
				currentTag.replace(db, userIdString, analog);
		}
		// show("handleMarkingAfterDeleting");
		newTags.show("NewTagsAfterDeleting");
		newTags.save();
	} // End of handleMarking

	
	
	public void save() {
		for (int m = 0; m < size(); m++)
			get(m).save(db, userIdString);
	} // End of save
	
	
	public void show(String name) {
		System.out.println("-----------------------------");
		String suffix = "";
		if (isEmpty())
			suffix = " is empty ";
		System.out.println("Tags object: " + name + suffix);
		for (int m = 0; m < size(); m++)
			get(m).show();
	}
// ************************************************************************************** //
/*void formPhotosArray() {
	String sql = String.format("SELECT id,path from Photos WHERE userid=%s",userIdString);
	photos = db.SqlToHashTableList(sql,"id","path");
} // End of formPhotosArray


public String getPhotos() {
	formPhotosArray();
	return Utils.HashTableListToJson(photos,"id","path");
} // End of getPhotos
*/


	public String getTags() {
		String sql = String.format("SELECT id, name from Tags WHERE userid=%s ORDER BY name", userIdString);
		ArrayList<Hashtable<String, String>> tags = db.SqlToHashTableList(sql, "id", "name");
		System.out.println(tags);
		return Utils.HashTableListToJson(tags, "id", "name");
	} // End of getTags


ArrayList<Hashtable<String, String>> formTagRelationList() {
	ArrayList<Hashtable<String, String>> result = new ArrayList<Hashtable<String, String>>();
	int n = photos.size();
	for (int m = 0; m < n - 1; m++) {
		String sql = String.format("SELECT photoid, tagid from Relation WHERE  userid=%1s AND photoid=%2s",
				userIdString, photos.get(m).get("id"));
		result.addAll(db.SqlToHashTableList(sql, "photoid", "tagid"));
	}
	return result;
}

public String getTagRelation() {
	return Utils.HashTableListToJson(formTagRelationList(),"photoid","tagid");
	} // End of getTagRelation



}
