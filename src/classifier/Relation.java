package classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Relation {
	PhotosDatabase db;
	String userIdString;
	public	Relation (PhotosDatabase db, String userIdString) {
		this.db = db;
		this.userIdString=userIdString;
		String executedString = "CREATE TABLE IF NOT EXISTS Relation (userid integer, photoid integer, tagid integer)";
		db.executeUpdate(executedString);
	} // End of constructor

	
	
// ******************* not changed **********************************************************	
	
	public ArrayList<RelationElement> sampleForMarking(ArrayList<PhotoForBrowser> photos) {
		ArrayList<RelationElement> result = new ArrayList<RelationElement>();
		for (int n = 0; n < photos.size(); n++) {
			String sql = String.format(
					"SELECT photoid, tagid from Relation WHERE userid=%1s AND photoid=%2s ORDER BY tagid", userIdString,
					photos.get(n).getId());
			ArrayList<Hashtable<String, String>> pairs = db.SqlToHashTableList(sql, "photoid", "tagid");
			for (int m = 0; m < pairs.size(); m++) {
				RelationElement element = new RelationElement();
				element.setFields(pairs.get(m).get("photoid"), pairs.get(m).get("tagid"));
				result.add(element);
			}
		}
		return result;
	} // End of sampleForMarking
	
	

	public ArrayList<String> sampleOfOnePhoto(String photoid) {
		String sql = String.format("SELECT tagid from Relation WHERE userid=%1s AND photoid=%2s ORDER BY tagid",
				userIdString, photoid);
		return db.arrayListOfColumn(sql);
	} // End of sampleForMarking
	
	
	
	public String jsonSampleForMarking(ArrayList<PhotoForBrowser> photos) throws JsonProcessingException {
		ArrayList<RelationElement> sample = sampleForMarking(photos);
		ObjectMapper om = new ObjectMapper();
		ArrayList<String> array = new ArrayList<String>();
		for (int m = 0; m < sample.size(); m++)
			array.add(om.writeValueAsString(sample.get(m)));
		return Utils.jsonListIntoBrackets(array);
	} // End of jsonSampleForMarking
		

		/*public void sampleFromMarking(String jsonArray) throws JsonParseException, JsonMappingException, IOException {
			ObjectMapper om = new ObjectMapper();
			ArrayList<Tag> array = om.readValue(jsonArray, new TypeReference<ArrayList<Tag>>() {
			});
			clear();
			addAll(array);
		} // End of sampleFromMarking
		*/
		
	
		public RelationElement retriveAnalog(ArrayList<RelationElement> sample,  RelationElement element) {
			for (int m = 0; m < sample.size(); m++) {
				RelationElement currentElement = sample.get(m);
				if ( (currentElement.getPhotoid().compareTo(element.getPhotoid()) == 0) &&
						(currentElement.getTagid().compareTo(element.getTagid())==0)   )
				{  sample.remove(m); // show("in retrive");
					return currentElement;
				}
			}
			return null;
		} // End of retriveAnalog
		
		
		
		
		
		public void handleMarking(String photoid, String jsonNewTagIds)  {
	   // show("handleMarkingBeforeDeleting"); 
			ArrayList<String> newTagIds = new ArrayList<String>(Arrays.asList(jsonNewTagIds.split(",")));
			ArrayList<String> oldTagIds=sampleOfOnePhoto(photoid);
			ArrayList<String> deleted=Utils.leftWithoutRight(oldTagIds,newTagIds);
			//System.out.println("deleted="+deleted.toArray().toString()); 
			Utils.showArrayList(deleted,"deleted");
			delete(photoid,deleted);
			ArrayList<String> added=Utils.leftWithoutRight(newTagIds,oldTagIds);
			Utils.showArrayList(added,"added");
			//System.out.println("added="+added.toArray().toString());
			save(photoid,added);
		} // End of handleMarking

		
		
		
	public void save(String photoid, ArrayList<String> tagids) {
		for (int m = 0; m < tagids.size(); m++) {
			RelationElement pair = new RelationElement();
			pair.setFields(photoid, tagids.get(m));
			pair.save(db, userIdString);
		}
	} // End of save
		
	public void delete(String photoid, ArrayList<String> tagids) {
		for (int m = 0; m < tagids.size(); m++) {
			RelationElement pair = new RelationElement();
			pair.setFields(photoid, tagids.get(m));
			pair.delete(db,userIdString);
		}
	} // End of save
	
	
	
	
	
	/*	public void show(String photoid,String name) {
			System.out.println("-----------------------------");
			String suffix = "";
			if (isEmpty())
				suffix = " is empty ";
			System.out.println("Tags object: " + name + suffix);
			for (int m = 0; m < size(); m++)
				get(m).show();
		}	
	*/
	
	
	
	
	
	
} // End of class
