import java.sql.Connection;
import java.sql.DriverManager;

public class Admin {
	private String email;
	private String pass;
	private boolean loggedIn;
	
<<<<<<< HEAD
	public Admin() {
=======
	public User() {
>>>>>>> 87332a5db99f869a2672b6c9573426df356f3ab2
		this.loggedIn = false;
		this.email = null;
		this.pass = null;
	}
	
<<<<<<< HEAD
	public Admin(String email, String pas) {
=======
	public User(String email, String pas) {
>>>>>>> 87332a5db99f869a2672b6c9573426df356f3ab2
		this.loggedIn = true;
		this.email = email;
		this.pass = pas;
	}
	
<<<<<<< HEAD
=======
	public String getUserType() {
		return userType;
	}
	
>>>>>>> 87332a5db99f869a2672b6c9573426df356f3ab2
	public boolean isLoggedIn() {
		return loggedIn;
	}
}
