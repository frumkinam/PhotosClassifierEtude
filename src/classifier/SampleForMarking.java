package classifier;

import java.util.ArrayList;
import java.util.Hashtable;

public class SampleForMarking {
	PhotosDatabase db;
	String userIdString;
	ArrayList<Hashtable<String,String>> photos;
	
	public SampleForMarking(PhotosDatabase db, String userIdString) {
		this.db = db;
		this.userIdString=userIdString;
	}

	void formPhotosArray() {
		String sql = String.format("SELECT id,path from Photos WHERE userid=%s",userIdString);
		photos = db.SqlToHashTableList(sql,"id","path");
	} // End of formPhotosArray

	
	public String getPhotos() {
		formPhotosArray();
		int n = photos.size();
		String result = "[";
		for (int m = 0; m < n - 1; m++) {
			result += Utils.jsonElement("id", photos.get(m).get("id"), "path", photos.get(m).get("path")) + ",";
		}
		if (n > 0)
			result +=  Utils.jsonElement("id", photos.get(n - 1).get("id"), "path",
					photos.get(n - 1).get("path"));
		result += "]";
		return result;
	} // End of getPhotos

	
	
	public String getTags() {
		String sql = String.format("SELECT id, name from Tags WHERE userid=%s", userIdString);
		ArrayList<Hashtable<String, String>> tags = db.SqlToHashTableList(sql, "id", "name");
		int n = tags.size();
		String result = "[";
		for (int m = 0; m < n - 1; m++) {
			result += Utils.jsonElement("id", tags.get(m).get("id"), "name", tags.get(m).get("name")) + ",";
		}
		if (n > 0)
			result +=  Utils.jsonElement("id", tags.get(n - 1).get("id"), "name",
					tags.get(n - 1).get("name"));
		result += "]";
		return result;
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
	
	
} // End of class
