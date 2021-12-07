import java.sql.Connection;
import java.sql.DriverManager;

public class Admin {
	private String email;
	private String pass;
	private boolean loggedIn;
	
	public User() {
		this.loggedIn = false;
		this.email = null;
		this.pass = null;
	}
	
	public User(String email, String pas) {
		this.loggedIn = true;
		this.email = email;
		this.pass = pas;
	}
	
	public String getUserType() {
		return userType;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
}
