import java.sql.Connection;
import java.sql.DriverManager;

public class Admin {
	private String email;
	private String pass;
	private boolean loggedIn;

	public Admin() {
		this.loggedIn = false;
		this.email = null;
		this.pass = null;
	}

	public Admin(String email, String pas) {
		this.loggedIn = true;
		this.email = email;
		this.pass = pas;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}
}
