package classifier;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sources.PhotoHandler;
import sources.Source;

public class Photos {
	
	PhotosDatabase db;
	String userIdString;
	
public	Photos (PhotosDatabase db, String userIdString) {
		this.db = db;
		this.userIdString=userIdString;
		db.createTable("Photos", "userid", "integer", "sourceid", "integer", "location", "text", "name", "text",
				"hash", "text", "date", "integer", "metadata", "text", "original", "integer");
	} // End of constructor

public long totalNumber() {
	return db.totalLength("Photos",userIdString);
} // End of totalNumber

public long numberOfDoubles() {
	String where= " userid="+userIdString+" AND original IS NOT NULL";
	return db.tableCount("Photos",where);
}

// Output of this procedure is the protocol for browser
public String scanning() {
	ArrayList<String> result = new ArrayList<String>();
		ArrayList<Hashtable<String, String>> tbl = new Sources(db, userIdString).getIdsAndSources();
		for (int m = 0; m < tbl.size(); m++) {
			// System.out.println("id=" + tbl.get(m).get("sourceid") + " source=" +
			// tbl.get(m).get("source"));
			String sourceId = tbl.get(m).get("sourceid");
			// Path sourcePath = Paths.get(tbl.get(m).get("source"));
			String sourcePath = tbl.get(m).get("source");
			result.addAll(scanningSource(sourceId, sourcePath)); 
		} // End of for
		return Utils.arrayListToString(result);	
	} // End of scanning

	class ScanningHandler extends Photo implements PhotoHandler {
		ArrayList<String> protocol;
		PhotosDatabase db;
		String userIdString;
		String sourceId;

		ScanningHandler(PhotosDatabase db, String userIdString, String sourceId, ArrayList<String> protocol) {
			this.db = db;
			this.userIdString = userIdString;
			this.sourceId=sourceId;
			this.protocol=protocol;
		} // End of constructor

		public void process() {
			super.process();
			if (!isRegistrated(db, userIdString, sourceId)) {
				registrate(db, userIdString, sourceId);
				protocol.add(super.toString());
			}
		} // End of process
		
	} // End of class ScanningHandler

// Here output ArrayList is a part of protocol
	public ArrayList<String> scanningSource(String sourceId, String sourcePath) {
			ArrayList<String> result = new ArrayList<String>();
			ScanningHandler handler= new ScanningHandler(db,userIdString,sourceId,result);
			Source source= Sources.getObject(sourcePath);
			source.traversal(handler);
			return result;
	} // End of scanningSource
	
	

	public String searchingDeleted() {
	
		ArrayList<String> protocol = new ArrayList<String>();
		String sql = String.format(
				"SELECT Photos.id, path,location,name  FROM  Photos inner join Sources on Photos.sourceid = Sources.id and Photos.userid=%1s and Sources.userid=%2s",
				userIdString, userIdString);
		ArrayList<Hashtable<String, String>> sample = db.SqlToHashTableList(sql, "id", "source", "location", "name");
		for (int m = 0; m < sample.size(); m++) {
			PhotoPath path = new PhotoPath(sample.get(m).get("source"), sample.get(m).get("location"),
					sample.get(m).get("name"));
			Source source = Sources.getObject(path.source);
			if (!source.contains(path.location, path.name)) {
				String id = sample.get(m).get("id");
				changeOriginals(id);
				sql = "DELETE FROM Photos WHERE id=" + id;
				db.executeUpdate(sql);
				String forProtocol = "Photo " + path.getPathString() + " has deleted from database";
				System.out.println(forProtocol);
				protocol.add(forProtocol);
			}
		}
		return Utils.arrayListToString(protocol);
	} // End of searchingDeleted

	void changeOriginals(String id) {
		String sql = "SELECT id FROM Photos WHERE original=" + id;
		ArrayList<String> sample = db.arrayListOfColumn(sql);
		if (sample.isEmpty())
			return;
		String newOriginal = sample.get(0);
		sql = String.format("UPDATE Photos SET original=null WHERE id=%s", newOriginal);
		db.executeUpdate(sql);
		for (int m = 1; m < sample.size(); m++) {
			sql = String.format("UPDATE Photos SET original=%1s WHERE id=%2s", newOriginal, sample.get(m));
			db.executeUpdate(sql);
		}
	} // End of changeOriginals
	

	// ----------------------------   M a r k i n g   ------------------------------------------
	
	ArrayList<Hashtable<String, String>> allPhotosForMarking() {
		String sql = String.format("SELECT id,path from Photos WHERE userid=%s",userIdString);
		return db.SqlToHashTableList(sql,"id","path");
	} // End of formPhotosArray


	public String  jsonAllPhotosForMarking()  {
		ArrayList<Hashtable<String, String>> photos=allPhotosForMarking();
		return Utils.HashTableListToJson(photos,"id","path");
	} // End of getPhotos
	
	
	public ArrayList<PhotoForBrowser> sampleForMarking() {
		// ArrayList<PhotoForBrowser> result = new ArrayList<PhotoForBrowser>();
		String sql = String.format(
				"SELECT Photos.id, path,location,name,date  FROM  Photos inner join Sources "
						+ "on Photos.sourceid = Sources.id and Photos.userid=%1s and Sources.userid=%2s",
				userIdString, userIdString);
		ArrayList<Hashtable<String, String>> sample = db.SqlToHashTableList(sql, "id", "source", "location", "name",
				"date");
		/*for (int m = 0; m < sample.size(); m++) {
			PhotoPath path = new PhotoPath(sample.get(m).get("source"), sample.get(m).get("location"),
					sample.get(m).get("name"));
			PhotoForBrowser photo = new PhotoForBrowser();
			photo.setId(sample.get(m).get("id"));
			photo.setPath(path.getPathString());
			photo.setName(path.name);
			Date d = new Date(Long.valueOf(sample.get(m).get("date")));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
			photo.setDate(dateFormat.format(d));
			result.add(photo);
		} // End of for */
		return  photosForBrowser(sample);
	} // End of sampleForMarking
	
	
	public ArrayList<PhotoForBrowser> sampleForMarking(String startdate, String finishdate) {
		String dateLimitations = formDateLimitations(startdate, finishdate);
		String sql = String.format(
				"SELECT Photos.id, path,location,name,date  FROM  Photos inner join Sources "
						+ "on Photos.sourceid = Sources.id and Photos.userid=%1s and Sources.userid=%2s",
				userIdString, userIdString);
		sql += " WHERE " + dateLimitations;
		ArrayList<Hashtable<String, String>> sample = db.SqlToHashTableList(sql, "id", "source", "location", "name",
				"date");
		return photosForBrowser(sample);
	} // End of sampleForMarking
	
	
	public ArrayList<PhotoForBrowser> sampleForMarkingWithoutTags(String startdate, String finishdate) {

		String dateLimitations = formDateLimitations(startdate, finishdate);
		String sql = String.format(
				"SELECT Photos.id, path,location,name,date  FROM  Photos inner join Sources "
						+ "on Photos.sourceid = Sources.id and Photos.userid=%1s and Sources.userid=%2s",
				userIdString, userIdString);
		sql += " WHERE " + dateLimitations + " AND Photos.id not in (select photoid from Relation)";
		ArrayList<Hashtable<String, String>> sample = db.SqlToHashTableList(sql, "id", "source", "location", "name",
				"date");
		return photosForBrowser(sample);
	} // End of sampleForMarkingWithoutTags
	
	
	
	ArrayList<PhotoForBrowser> photosForBrowser(ArrayList<Hashtable<String, String>> sample){
		ArrayList<PhotoForBrowser> result = new ArrayList<PhotoForBrowser>();
		for (int m = 0; m < sample.size(); m++) {
			PhotoPath path = new PhotoPath(sample.get(m).get("source"), sample.get(m).get("location"),
					sample.get(m).get("name"));
			PhotoForBrowser photo = new PhotoForBrowser();
			photo.setId(sample.get(m).get("id"));
			photo.setPath(path.getPathString());
			photo.setName(path.name);
			Date d = new Date(Long.valueOf(sample.get(m).get("date")));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
			photo.setDate(dateFormat.format(d));
			result.add(photo);
		} // End of for
		return result;
	} // End of photosForBrowser
	
	
	public String SampleForMarkingToJson(ArrayList<PhotoForBrowser> A) throws JsonProcessingException {
		ObjectMapper om = new ObjectMapper();
		ArrayList<String> array = new ArrayList<String>();
		for (int m = 0; m < A.size(); m++)
			array.add(om.writeValueAsString(A.get(m)));
		return Utils.jsonListIntoBrackets(array);
	}
	

	public ArrayList<PhotoForBrowser> sampleForMarking(String startdate, String finishdate, String jsonTags) throws JsonParseException, JsonMappingException, IOException
			 {
		String dateLimitations = formDateLimitations(startdate, finishdate);
		/*ObjectMapper om = new ObjectMapper();
		 System.out.println("sampleForMarking  jsonTags=" + jsonTags);
		ArrayList<TagsQueryElement> tags = om.readValue(jsonTags, new TypeReference<ArrayList<TagsQueryElement>>() {
		});*/
		ArrayList<PhotoForBrowser> result = new ArrayList<PhotoForBrowser>();
		String tagsLimitations = formTagsLimitations(jsonTags);

		String sql = String.format(
				"SELECT Photos.id, path, location, name, date  FROM  Photos inner join Sources "
						+ "ON Photos.sourceid = Sources.id and Photos.userid=%1s and Sources.userid=%2s"
						+ " inner join Relation on  Relation.userid=%3s and Photos.id=photoid",
				userIdString, userIdString, userIdString);
		sql += tagsLimitations;
		sql += " WHERE " + dateLimitations;

		/*
		 * SELECT Photos.id, path,location,name,date FROM Photos inner join Sources on
		 * Photos.sourceid = Sources.id and Photos.userid=1 and Sources.userid=1 inner
		 * join Relation on Relation.userid=1 and Photos.id=photoid and ( tagid=20 or
		 * tagid=9 )
		 * 
		 * Photos.id, path,location,name,date FROM Photos inner join Sources on
		 * Photos.sourceid = Sources.id and Photos.userid=1 and Sources.userid=1 inner
		 * join Relation on Relation.userid=1 and Photos.id=photoid and tagid<17
		 * date>=1471414501000 and date<=1511362938000
		 * 
		 */

		ArrayList<Hashtable<String, String>> sample = db.SqlToHashTableList(sql, "id", "source", "location", "name",
				"date");

		for (int m = 0; m < sample.size(); m++) {
			PhotoPath path = new PhotoPath(sample.get(m).get("source"), sample.get(m).get("location"),
					sample.get(m).get("name"));
			PhotoForBrowser photo = new PhotoForBrowser();
			photo.setId(sample.get(m).get("id"));
			photo.setPath(path.getPathString());
			photo.setName(path.name);
			Date d = new Date(Long.valueOf(sample.get(m).get("date")));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
			photo.setDate(dateFormat.format(d));
			result.add(photo);
		} // End of for
		return result;
	} // End of sampleForMarking
	
	String formDateLimitations(String startdate, String finishdate) {
		String startDate = Utils.dateFromBrowser(startdate);
		String finishDate = Utils.dateFromBrowser(finishdate);
		return " date>=" + startDate + " AND date<=" + finishDate;
	} // End of formDateLimitations
	
	
	String formTagsLimitations(String jsonTags) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		System.out.println("sampleForMarking  jsonTags=" + jsonTags);
		ArrayList<TagsQueryElement> tags = om.readValue(jsonTags, new TypeReference<ArrayList<TagsQueryElement>>() {
		});
		String result = "";
		for (int m = 0; m < tags.size(); m++) {
			switch (tags.get(m).getUsing()) {
			case "include": {
				result += " AND tagid=" + tags.get(m).getId();
				break;
			}
			case "exclude": {
				result += " AND tagid!=" + tags.get(m).getId();
				break;
			}
			} // End of switch
		}
		return result;
	} // End of formTagsLimitations
	

	public ArrayList<Hashtable<String, String>> selecting(String command, String startdate, String finishdate,
			String jsonTags) throws JsonParseException, JsonMappingException, IOException {
		String dateLimitations = formDateLimitations(startdate, finishdate);
		String sql = "";
		switch (command) {
		case "all": {
			sql = String.format(
					"SELECT path,location,name,date  FROM  Photos inner join Sources "
							+ "on Photos.sourceid = Sources.id and Photos.userid=%1s and Sources.userid=%2s",
					userIdString, userIdString);
			sql += " WHERE " + dateLimitations;
			break;
		}
		case "withouttags": {
			sql = String.format(
					"SELECT  path,location,name,date  FROM  Photos inner join Sources "
							+ "on Photos.sourceid = Sources.id and Photos.userid=%1s and Sources.userid=%2s",
					userIdString, userIdString);
			sql += " WHERE " + dateLimitations + " AND Photos.id not in (select photoid from Relation)";
			break;
		}
		case "bytags": {
			String tagsLimitations = formTagsLimitations(jsonTags);
			sql = String.format(
					"SELECT  path, location, name, date  FROM  Photos inner join Sources "
							+ "ON Photos.sourceid = Sources.id and Photos.userid=%1s and Sources.userid=%2s"
							+ " inner join Relation on  Relation.userid=%3s and Photos.id=photoid",
					userIdString, userIdString, userIdString);
			sql += tagsLimitations;
			sql += " WHERE " + dateLimitations;
		}
		} // end of switch
		 System.out.println(" command = " +  command);
		 System.out.println("sql = " + sql);
		return db.SqlToHashTableList(sql, "source", "location", "name", "date");
	} // End of sampleForSelecting
	
	public String copySelectedPhotos(String location, String catalog, ArrayList<Hashtable<String, String>> sample) {
		ArrayList<String> protocol = new ArrayList<String>();
			for (int m = 0; m<sample.size(); m++) {
				PhotoPath path = new PhotoPath(sample.get(m).get("source"),sample.get(m).get("location"),sample.get(m).get("name"));
				Map<String,String> date = Utils.dateForPhotoPlacing(sample.get(m).get("date"));
				protocol.add(path.copy(location,catalog,date)); 
			} // End of for
			return Utils.arrayListToString(protocol);	
		} // End of scanning
	
	public String sampleToReferences(ArrayList<Hashtable<String, String>> sample) {
		ArrayList<String> protocol = new ArrayList<String>();
			for (int m = 0; m<sample.size(); m++) {
				PhotoPath path = new PhotoPath(sample.get(m).get("source"),sample.get(m).get("location"),sample.get(m).get("name"));
				protocol.add(path.getReference()); 
			} // End of for
			return Utils.arrayListToString(protocol);	
		} // End of scanning 	
	
// ---- assembling ------
	public ArrayList<Hashtable<String, String>> assembling() {
		String sql = String.format(
				"SELECT path,location,name,date  FROM  Photos inner join Sources "
						+ "on Photos.sourceid = Sources.id and Photos.userid=%1s and Sources.userid=%2s",
				userIdString, userIdString);
		return db.SqlToHashTableList(sql, "source", "location", "name", "date");
	} // End of sampleForSelecting
	

	
} // End of class    



/*
   previous variant 
public ArrayList<String> scanningSource(String sourceId, Path sourcePath) {
		// String s=AddedDirectory.toString();
		ArrayList<String> result = new ArrayList<String>();
		try {
			Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() { // @Override
				// refuses to act without BasicFileAttributes attrs !!!
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					System.out.println("Considered " + file);
					if (Photo.isPhoto(file)) {
						Photo currentPhoto = new Photo(sourcePath, file);
						if (!currentPhoto.isRegistrated(db, userIdString, sourceId)) {
							currentPhoto.registrate(db, userIdString, sourceId);
							result.add(currentPhoto.toString());
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			System.out.println("Mistake in the directory travel" + e);
			return result;
		}
		return result;
	} // End of add

*/
