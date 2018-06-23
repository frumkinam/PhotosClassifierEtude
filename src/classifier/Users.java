package classifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Users {
PhotosDatabase db;
	
public	Users(PhotosDatabase db) {
		this.db = db;
		db.createTable("Users", "login", "text", "password", "text", "language", "text");
	} // End of constructor

	public void addUser(User user) {
		String executedString = "INSERT INTO Users (login,password,language) VALUES('" + user.login + "','"
				+ user.password + "','" + user.language + "')";
		db.executeUpdate(executedString);
	} // End of addUser
	
	public void addUser(String login,String password,String language) {
		String executedString = "INSERT INTO Users (login,password,language) VALUES('" + login + "','"
				+ password + "','" + language + "')";
		db.executeUpdate(executedString);
		
	} // End of addUser
	
	
	
	public User getUser(String login) {
    User user = null;
	String executedString = String.format("SELECT * FROM Users WHERE login='%s'", login);
	try {
		ResultSet rs = db.statement.executeQuery(executedString);
		if (rs.next()) {
			user = new User();
			user.id = rs.getLong("id");
			user.login = rs.getString("login");
			user.password = rs.getString("password");
			user.language = rs.getString("language");
			rs.close();
		}
	} catch (SQLException e) {
		System.out.println(e);
	}
	return user;
} // End of getUser

	
	public String getLoginById(String id) {
		ArrayList<String> alc = db.arrayListOfColumn(String.format("SELECT login FROM Users WHERE id='%s'", id));
		String result = null;
		if (alc != null)
			result = alc.get(0);
		return result;
	} // End of getLoginById
	
} // End of class
