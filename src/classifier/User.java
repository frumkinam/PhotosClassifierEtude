package classifier;
import java.lang.System;

public class User {
	public long id;
	public String login;
	public String password;
	public String language; // language
	
	
	public User() {
		id = 0;
		login = "";
		password = "";
		language = "";
	}
	
	
public	void show() {
		System.out.println("id = "+id);
		System.out.println("login = "+login);
		System.out.println("password = "+password);
		System.out.println("language = "+language); 
			}
	
/*public	boolean loginExists(Users users) {
		return users.userExists(login);
	} // End of loginExists
*/ 	
public	boolean containsEmptyField() {
		return (login.isEmpty() || password.isEmpty() || language.isEmpty());
	} // End of containsEmptyField

} // End of class
