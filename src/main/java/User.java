import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import io.javalin.http.Context;

public class User {
	//user class attributes
	private String fname;
	private String lname;
	private String contactn;
	private String creditcardnum;
	private int cvv;
	private int exp;
	private String email;
	private String pass;
	private boolean loggedIn;
	private int id;
	
	//default constructor, initializing not logged in user
	public User() {
		this.loggedIn = false;
		this.fname = null;
		this.lname = null;
		this.contactn = null;
		this.creditcardnum = null;
		this.cvv = 0;
		this.exp = 0;
		this.email = null;
		this.pass = null;
		this.id = 0;
	}
	
	//constructor with input, for when a user logs in or registers
	public User(String first, String last, String contact, String ccnum, int crv, int exp, String emai, String pas, int id) {
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
	
	//getter method for retrieving someones full name
	public String getFullName() {
		return fname + " " + lname;
	}

	//getter method for retrieving someones contact number
    public String getContact() {
        return contactn;
    }

    //getter method for retrieving someones credit card number
    public String getCCN() {
        return creditcardnum;
    }

    //getter method for retrieving someones credit card expiration date
    public int getEXP() {
        return exp;
    }

    //getter method for retrieving someones cvv
    public int getCVV() {
        return cvv;
    }

    //getter method for retrieving someones email
    public String getEmail() {
        return email;
    }

    //getter method for retrieving someones password
    public String getPass() {
        return pass;
    }
    
    //getter method for retrieving someones ID
    public int getId() {
    	return id;
    }
	
    //getter method for retrieving someones isloggedin variable
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	//viewflights: returns an arraylist of flights that go to a specific destination, as the user input in the ctx variable
	public static ArrayList<Flight> viewFlights(Context ctx, Connection con) throws SQLException {
		String dest = ctx.formParam("destination");
    	String flightsquery = "SELECT * FROM Flight WHERE destination_loc = '" + dest + "'";
    	Statement stmt = con.createStatement();
    	ResultSet rs = stmt.executeQuery(flightsquery);
		ArrayList<Flight> flights = new ArrayList<Flight>();
		while (rs.next()) {
    		String s = rs.getString("departing_loc");
			String date = rs.getString("date");
			String time = rs.getString("time");
			int id = rs.getInt("flight_id");
			int sn = rs.getInt("total_seats");
			int p = rs.getInt("price");
			Flight f = new Flight(s, dest, date, time, sn, p, id);
			if (f.isValidDate() && f.isValidSeat()) {
				flights.add(f);
			}
    	}
		rs.close();
		stmt.close();
		return flights;
	}
	
	//bookflight: returns a flight that the user has booked, also adding it into the MySQL Books table, and updating total seats to be 1 less
	//since one seat was taken with the user booking one
	public static Flight bookFlight(Context ctx, Connection con, User curUser) throws SQLException {
		int flight_id = Integer.parseInt(ctx.formParam("fid"));
    	int seat_no = Main.getSeatNumber(flight_id,con);
    	String inputquery = String.format("INSERT INTO Books(customer_id, flight_id, seatn) values(%d,%d,%d)",curUser.getId(),flight_id,seat_no);
    	Statement stmt = con.createStatement();
    	stmt.executeUpdate(inputquery);
    	String s = ctx.formParam("start");
		String dest = ctx.formParam("dest");
		String date = ctx.formParam("date");
		String time = ctx.formParam("time");
		int p = Integer.parseInt(ctx.formParam("price"));
		Flight f = new Flight(s, dest, date, time, seat_no, p, flight_id);
		String updatequery = "UPDATE Flight SET total_seats = total_seats - 1 WHERE flight_id = " + flight_id;
		stmt.executeUpdate(updatequery);
		stmt.close();
		return f;
	}
	
	//cancelflight: cancels a flight for a user if they click cancel next to one of their scheduled flights, removing it from MySQL and also
	//updating total seats to be 1 more, since a new seat has opened up now that the user has canceled
	public static int cancelFlight(Context ctx, Connection con, User curUser) throws SQLException {
		int flight_id = Integer.parseInt(ctx.formParam("fid"));
    	int seat_no = Integer.parseInt(ctx.formParam("seat"));
    	int index = Integer.parseInt(ctx.formParam("index")) - 1;
    	String deletequery = String.format("DELETE FROM Books WHERE flight_id = %d AND customer_id = %d AND seatn = %d",flight_id,curUser.getId(),seat_no);
    	Statement stmt = con.createStatement();
    	stmt.executeUpdate(deletequery);
		String updatequery = "UPDATE Flight SET total_seats = total_seats + 1 WHERE flight_id = " + flight_id;
		stmt.executeUpdate(updatequery);
		stmt.close();
		return index;
	}
}
