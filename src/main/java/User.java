
public class User {
	private String fname;
	private String lname;
	private int contactn;
	private int creditcardnum;
	private int cvv;
	private String exp;
	private String email;
	private String pass;
	private String userType;
	private boolean loggedIn;
	
	public User() {
		this.loggedIn = false;
		this.fname = null;
		this.lname = null;
		this.contactn = 0;
		this.creditcardnum = 0;
		this.cvv = 0;
		this.exp = null;
		this.email = null;
		this.pass = null;
		this.userType = null;
	}
	
	public User(String first, String last, int contact, int ccnum, int crvv, String expd, String emai, String pas) {
		this.loggedIn = true;
		this.fname = first;
		this.lname = last;
		this.contactn = contact;
		this.creditcardnum = ccnum;
		this.cvv = crvv;
		this.exp = expd;
		this.email = emai;
		this.pass = pas;
		this.userType = "user";
	}
	
	public String getFullName() {
		return fname + " " + lname;
	}
	
	public String getUserType() {
		return userType;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
}
