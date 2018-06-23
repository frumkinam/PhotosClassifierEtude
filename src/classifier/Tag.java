package classifier;

public class Tag {
	private String id;
	private String name;
	private String description;
	
	public void setFields(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	void save(PhotosDatabase db, String userId) {
		String sql = "INSERT INTO Tags (userid,name,description)"
				+ String.format("Values(%1s,'%2s','%3s')",userId, name, description);
		db.executeUpdate(sql);
	}

	void delete(PhotosDatabase db, String userId) {
		String sql = String.format("DELETE FROM Tags WHERE id=%1s AND userid=%2s", id, userId);
		db.executeUpdate(sql);
	} // End of delete

	void deleteWithRelation(PhotosDatabase db, String userId) {
		String sql = String.format("DELETE FROM Relation WHERE  userid=%1s AND tagid=%2s",userId,id);
		db.executeUpdate(sql);
		delete(db,userId);
	}
	
	
	void replace(PhotosDatabase db, String userId, Tag tag) {
		String sql = String.format("UPDATE Tags SET name='%1s', description='%2s' WHERE id=%3s", tag.getName(),
				tag.getDescription(), id);
		db.executeUpdate(sql);
	} // End of replace
	
	void show() {
		System.out.println("id = " + getId() + ";  name= " + getName() + ";  description= "+ getDescription());
	}
} // End of class
