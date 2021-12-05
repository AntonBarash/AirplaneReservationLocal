
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
	public User(String first, String last, int contact, int ccnum, int crvv, String expd, String emai, String pas) {
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
}
