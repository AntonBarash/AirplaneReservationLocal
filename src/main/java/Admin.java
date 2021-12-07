import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import io.javalin.http.Context;

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
	
	public static void addFlight(Context ctx, Connection con) throws SQLException {
		String start = ctx.formParam("start");
		String dest = ctx.formParam("destination");
		String date = ctx.formParam("departuredate");
		String time = ctx.formParam("departuretime");
		int seats = Integer.parseInt(ctx.formParam("seats"));
		int price = Integer.parseInt(ctx.formParam("price"));
		Statement stmt = con.createStatement();
		String insertquery = String.format("INSERT INTO Flight(time,destination_loc,departing_loc,date,total_seats,price) values('%s', '%s', '%s', '%s', %d, %d)",time,dest,start,date,seats,price);	
		stmt.executeUpdate(insertquery);
		stmt.close();
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
			if (f.isValidDate()) {
				flights.add(f);
			}
    	}
    	rs.close();
		stmt.close();
    	return flights;
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
	
	public static void removeFlight(Context ctx, Connection con) throws SQLException {
		int flight_id = Integer.parseInt(ctx.formParam("fid"));
    	String deletequery = "DELETE FROM Flight WHERE flight_id = " + flight_id;
    	Statement stmt = con.createStatement();
    	stmt.executeUpdate(deletequery);
    	stmt.close();
	}

	public static User searchCustomer(Context ctx, Connection con) throws SQLException {
		String cemail = ctx.formParam("cemail");
    	String custquery = "SELECT * FROM Customer WHERE email = '" + cemail + "'";
    	Statement stmt = con.createStatement();
    	ResultSet rs = stmt.executeQuery(custquery);
    	rs.next();
    	String fname = rs.getString("fname");
		String lname = rs.getString("lname");
		String pass = rs.getString("pass");
		int contactn = rs.getInt("contactn");
		int ccnum = rs.getInt("creditcardn");
		int ccv = rs.getInt("ccv");
		int exp = rs.getInt("exp");
		int custid = rs.getInt("customer_id");
		User curUser = new User(fname, lname, contactn, ccnum, ccv, exp, cemail, pass, custid);
		rs.close();
		stmt.close();
		return curUser;
	}
	
	public static ArrayList<Flight> searchCustomerFlights(Context ctx, Connection con, User curUser) throws SQLException {
		
		String flightquery = String.format("SELECT * FROM Books B, Flight F WHERE B.customer_id = %d AND B.flight_id = F.flight_id", curUser.getId());
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(flightquery);
		ArrayList<Flight> flights = new ArrayList<Flight>();
		while (rs.next()) {
			String s = rs.getString("departing_loc");
			String dest = rs.getString("destination_loc");
			String date = rs.getString("date");
			String time = rs.getString("time");
			int id = rs.getInt("flight_id");
			int sn = rs.getInt("seatn");
			int p = rs.getInt("price");
			Flight f = new Flight(s, dest, date, time, sn, p, id);
			flights.add(f);
		}
		rs.close();
		stmt.close();
		return flights;
	}
}
