import java.sql.Connection;
import java.sql.DriverManager;

public class User {
	private String fname;
	private String lname;
	private int contactn;
	private int creditcardnum;
	private int cvv;
	private int exp;
	private String email;
	private String pass;
	private boolean loggedIn;
	private int id;
	
	public User() {
		this.loggedIn = false;
		this.fname = null;
		this.lname = null;
		this.contactn = 0;
		this.creditcardnum = 0;
		this.cvv = 0;
		this.exp = 0;
		this.email = null;
		this.pass = null;
		this.id = 0;
	}
	
	public User(String first, String last, int contact, int ccnum, int crv, int exp, String emai, String pas, int id) {
		this.loggedIn = true;
		this.fname = first;
		this.lname = last;
		this.contactn = contact;
		this.creditcardnum = ccnum;
		this.cvv = crv;
		this.exp = exp;
		this.email = emai;
		this.pass = pas;
		this.id = id;
	}
	
	public String getFullName() {
		return fname + " " + lname;
	}

    public int getContact() {
        return contactn;
    }

    public int getCCN() {
        return contactn;
    }

    public int getEXP() {
        return exp;
    }

    public int getCVV() {
        return cvv;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }
    
    public int getId() {
    	return id;
    }
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
}
