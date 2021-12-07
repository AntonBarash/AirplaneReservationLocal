import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import io.javalin.http.Context;

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
