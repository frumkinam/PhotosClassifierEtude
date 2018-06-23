package classifier;

public class RelationElement {
	private String photoid;
	private String tagid;
	
	
	public void setFields(String photoid, String tagid) {
		this.photoid = photoid;
		this.tagid = tagid;
	}
	
	public String getPhotoid() {
		return photoid;
	}
	public void setPhotoid(String photoid) {
		this.photoid = photoid;
	}
	public String getTagid() {
		return tagid;
	}
	public void setTagid(String tagid) {
		this.tagid = tagid;
	}

	
	void save(PhotosDatabase db, String userId) {
		String sql = "INSERT INTO Relation (userid,photoid,tagid)"
				+ String.format("Values(%1s,%2s,%3s)",userId,photoid,tagid);
		db.executeUpdate(sql);
	}

	void delete(PhotosDatabase db, String userId) {
		String sql = String.format("DELETE FROM Relation WHERE userid=%1s AND photoid=%2s AND tagid=%3s",userId,photoid,tagid);
		db.executeUpdate(sql);
	} // End of delete
	
	
	
	void show() {
		System.out.println("photoid = " + getPhotoid() + ";  tagid= " + getTagid());
	}
} // End of class
