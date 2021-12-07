import io.javalin.Javalin;
import java.util.concurrent.TimeUnit;
import io.javalin.http.staticfiles.Location;
import java.sql.*;
/*
public class Main {

  public static void main(String[] args) {
    Javalin app = Javalin.create()
        .start(getHerokuAssignedPort())
        .get("/", ctx -> ctx.result("Hello Heroku"));
  }

  private static int getHerokuAssignedPort() {
    String herokuPort = System.getenv("PORT");
    if (herokuPort != null) {
      return Integer.parseInt(herokuPort);
    }
    return 1000;
  }

}*/
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
public class Main {
	static User curUser = new User(); 
	static Admin curAdmin = new Admin();
	static ArrayList<Flight> userFlights = new ArrayList<Flight>();
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
    	
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/sub", Location.CLASSPATH);}
        	//config.addStaticFiles(staticFiles -> {staticFiles.directory = "/";});}
        //).start(getHerokuAssignedPort()); //FOR HEROKU DEPLOYMENT
        ).start(1053); //FOR LOCAL TESTING: INCREASE PORT NUMBER EACH TEST, SINCE OLD ONE IS ALREADY TAKEN WHEN RAN
        
        //mysql connection
        //Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://us-cdbr-east-04.cleardb.com/heroku_50d2532af7614cd?password=1a653a5a&reconnect=true&user=b6e662acb93d8f";
    	Connection con = DriverManager.getConnection(dbUrl);
        //Connection con = DriverManager.getConnection(dbUrl, "b6e662acb93d8f", "1a653a5a");
        
        //Statement stmt1 = con.createStatement();
        //ResultSet rs1 = stmt1.executeQuery("SELECT 1");
        //ResultSet rs2 = stmt1.executeQuery("SELECT 1");
        //Connection con=DriverManager.getConnection(  			THIS IS THE LOCAL DATABASE CONNECTION
        //	"jdbc:mysql://localhost:3306/AirplaneRes","root","romepage");
        	
        //change all of these renders to the correct html page
        
        app.get("/register", ctx -> {
        	ctx.render("/sub/register.vm");
        });
        
        app.post("/register", ctx -> {
        	//have SQL queries for inputting user data into database
        	String email = ctx.formParam("email");
        	Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer WHERE email = '" + email + "'");
        	if (rs.next()) {
        		Map<String, Integer> alreadyusedemail = new HashMap<String, Integer>() {{
                    put("usedemail", 1);
        		}};
        		ctx.render("/sub/register.vm",alreadyusedemail);
        	}
        	else {
        		String fname = ctx.formParam("fname");
        		String lname = ctx.formParam("lname");
        		int contactn = Integer.parseInt(ctx.formParam("contactn"));
        		int ccnum = Integer.parseInt(ctx.formParam("creditcard"));
        		int ccv = Integer.parseInt(ctx.formParam("cvv"));
        		int exp = Integer.parseInt(ctx.formParam("exp"));
        		String pass = ctx.formParam("password");
        		String inputquery = String.format("INSERT INTO Customer(fname,lname,contactn,creditcardn,ccv,exp,email,pass,account_type) values('%s','%s',%d,%d,%d,%d,'%s','%s',%d)", 
                				fname,lname,contactn,ccnum,ccv,exp,email,pass,0);
                stmt.executeUpdate(inputquery);
        		//String exp = ctx.formParam("exp");
                rs = stmt.executeQuery("SELECT customer_id FROM Customer WHERE email = '" + email + "'");
                rs.next();
                int id = rs.getInt("customer_id");
        		curUser = new User(fname, lname, contactn, ccnum, ccv, exp, email, pass, id);
        		//stmt = con.createStatement(); PRETTY SURE I DONT NEED THIS LINE, ONE STATEMENT CREATION IS ENOUGH
        		rs.close();
        		stmt.close();
        		ctx.render("/sub/customer.html");
        	} 
        });
        
        ArrayList<Integer> someList = new ArrayList<Integer>();
        someList.add(1);
        someList.add(2);
        ArrayList<Integer> xitem = new ArrayList<Integer>();
        xitem.add(1);
        Map<String, Object> loggedin2 = new HashMap<String, Object>() {{
            put("yo", "Hello");
            put("listItems",someList);
            put("xitem",xitem);
        }};
        app.get("/my-list", ctx -> {
        	Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Actor");
            rs.next();
            System.out.println(rs.getInt("year_born"));
            loggedin2.put("yo1",rs);
            ctx.render("/sub/my-list.vm", loggedin2);     	
        });
        
        app.post("/cancel", ctx -> {
        	//have SQL queries for checking login information, then check if admin or user account, depending on that, go to diff html page
            System.out.println(ctx.formParam("fid"));
        	ctx.render("/sub/my-list.vm");
        });
        
        app.get("/login", ctx -> {
            ctx.render("/sub/login.vm");
        });
        
        app.post("/login", ctx -> {
        	//have SQL queries for checking login information, then check if admin or user account, depending on that, go to diff html page
        	String email = ctx.formParam("email");
        	Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer WHERE email = '" + email + "'");
            if (rs.next()) {
            	String pass = ctx.formParam("password");
            	String correctpass = rs.getString("pass");
            	if (pass.equals(correctpass)) {
            		int accounttype = rs.getInt("account_type");
            		if (accounttype == 0) {
            			String fname = rs.getString("fname");
            			String lname = rs.getString("lname");
            			int contactn = rs.getInt("contactn");
            			int ccnum = rs.getInt("creditcardn");
            			int ccv = rs.getInt("ccv");
            			int exp = rs.getInt("exp");
            			int custid = rs.getInt("customer_id");
            			curUser = new User(fname, lname, contactn, ccnum, ccv, exp, email, pass, custid);
            			String flightquery = String.format("SELECT * FROM Books B, Flight F WHERE B.customer_id = %d AND B.flight_id = F.flight_id", custid);
            			rs = stmt.executeQuery(flightquery);
            			while (rs.next()) {
            				String s = rs.getString("departing_loc");
            				String dest = rs.getString("destination_loc");
            				String date = rs.getString("date");
            				String time = rs.getString("time");
            				int id = rs.getInt("flight_id");
            				int sn = rs.getInt("seatn");
            				int p = rs.getInt("price");
            				Flight f = new Flight(s, dest, date, time, sn, p, id);
            				userFlights.add(f);
            			}
            			rs.close();
                		stmt.close();
            			ctx.render("/sub/customer.html");
            		}
            		else {
            			String fname = rs.getString("fname");
            			String lname = rs.getString("lname");
            			Admin curAdmin = new Admin(email,pass);
            			rs.close();
                		stmt.close();
            			ctx.render("/sub/admin.html");
            		}
            	}
            	else {
            		Map<String, Integer> nothispass = new HashMap<String, Integer>() {{
                        put("wrongpass", 1);
            		}};
            		rs.close();
            		stmt.close();
            		ctx.render("/sub/login.vm",nothispass);
            	}
        	}
        	else {
        		Map<String, Integer> nothisemail = new HashMap<String, Integer>() {{
                    put("wrongemail", 1);
        		}};
        		rs.close();
        		stmt.close();
        		ctx.render("/sub/login.vm",nothisemail);
        	}
        });
        
        app.get("/logout", ctx -> {
        	curUser = new User();
        	curAdmin = new Admin();
        	userFlights = new ArrayList<Flight>();
            ctx.render("/sub/index.html");
        });
        
        app.get("/searchflights", ctx -> {
            ctx.render("/sub/searchflights.vm");
        });
        
        app.post("/searchflights", ctx -> {
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
        	Map<String, ArrayList<Flight>> allflights = new HashMap<String, ArrayList<Flight>>() {{
                put("flights",flights);
    		}};
    		rs.close();
    		stmt.close();
            ctx.render("/sub/searchflights.vm",allflights);
        });
        
        app.get("/customer", ctx -> {
            ctx.render("/sub/customer.html");
        });
        
        app.get("/admin", ctx -> {
        	curUser = new User();
        	userFlights = new ArrayList<Flight>();
            ctx.render("/sub/admin.html");
        });
        
        app.get("/customerflights", ctx -> {
        	Map<String, ArrayList<Flight>> booked = new HashMap<String, ArrayList<Flight>>() {{
                put("flights",userFlights);
    		}};
            ctx.render("/sub/customerflights.vm",booked);
        });
        
        app.post("/bookflight", ctx -> {
        	int flight_id = Integer.parseInt(ctx.formParam("fid"));
        	int seat_no = getSeatNumber(flight_id,con);
        	String inputquery = String.format("INSERT INTO Books(customer_id, flight_id, seatn) values(%d,%d,%d)",curUser.getId(),flight_id,seat_no);
        	Statement stmt = con.createStatement();
        	stmt.executeUpdate(inputquery);
        	//String inputquery = String.format("INSERT INTO Books(customer_id, flight_id, seatn) values(%d,%d,%d)",curUser.getId(),flight_id,seat_no);
        	//rs = stmt.executeQuery(selectquery);
        	String s = ctx.formParam("departing_loc");
			String dest = ctx.formParam("destination_loc");
			String date = ctx.formParam("date");
			String time = ctx.formParam("time");
			int p = Integer.parseInt(ctx.formParam("price"));
			Flight f = new Flight(s, dest, date, time, seat_no, p, flight_id);
			userFlights.add(f);
			String updatequery = "UPDATE Flight SET total_seats = total_seats - 1 WHERE flight_id = " + flight_id;
			stmt.executeUpdate(updatequery);
        	Map<String, Integer> booked = new HashMap<String, Integer>() {{
                put("booked",1);
    		}};
    		stmt.close();
            ctx.render("/sub/searchflights.vm",booked);
        });
        
        app.post("/customercancel", ctx -> {
        	int flight_id = Integer.parseInt(ctx.formParam("fid"));
        	int seat_no = Integer.parseInt(ctx.formParam("seat"));
        	int index = Integer.parseInt(ctx.formParam("index")) - 1;
        	String deletequery = String.format("DELETE FROM Books WHERE flight_id = %d AND customer_id = %d AND seatn = %d",flight_id,curUser.getId(),seat_no);
        	Statement stmt = con.createStatement();
        	stmt.executeUpdate(deletequery);
        	userFlights.remove(index);
			String updatequery = "UPDATE Flight SET total_seats = total_seats + 1 WHERE flight_id = " + flight_id;
			stmt.executeUpdate(updatequery);
        	Map<String, Object> canceled = new HashMap<String, Object>() {{
                put("canceled",1);
                put("flights",userFlights);
    		}};
    		stmt.close();
            ctx.render("/sub/customerflights.vm",canceled);
        });
        
        app.get("/customerinfo", ctx -> {
            ctx.render("/sub/customerinfo.vm");
        });
        
        app.post("/searchcustomer", ctx -> {
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
			curUser = new User(fname, lname, contactn, ccnum, ccv, exp, cemail, pass, custid);
			String flightquery = String.format("SELECT * FROM Books B, Flight F WHERE B.customer_id = %d AND B.flight_id = F.flight_id", custid);
			rs = stmt.executeQuery(flightquery);
			while (rs.next()) {
				String s = rs.getString("departing_loc");
				String dest = rs.getString("destination_loc");
				String date = rs.getString("date");
				String time = rs.getString("time");
				int id = rs.getInt("flight_id");
				int sn = rs.getInt("seatn");
				int p = rs.getInt("price");
				Flight f = new Flight(s, dest, date, time, sn, p, id);
				userFlights.add(f);
			}
			Map<String, Object> userinfo = new HashMap<String, Object>() {{
                put("cust",curUser);
                put("flights",userFlights);
    		}};
            ctx.render("/sub/customerinfo.vm",userinfo);
        });
        
        app.post("/admincancel", ctx -> {
        	int flight_id = Integer.parseInt(ctx.formParam("fid"));
        	int seat_no = Integer.parseInt(ctx.formParam("seat"));
        	int index = Integer.parseInt(ctx.formParam("index")) - 1;
        	String deletequery = String.format("DELETE FROM Books WHERE flight_id = %d AND customer_id = %d AND seatn = %d",flight_id,curUser.getId(),seat_no);
        	Statement stmt = con.createStatement();
        	stmt.executeUpdate(deletequery);
        	userFlights.remove(index);
			String updatequery = "UPDATE Flight SET total_seats = total_seats + 1 WHERE flight_id = " + flight_id;
			stmt.executeUpdate(updatequery);
        	Map<String, Object> canceled = new HashMap<String, Object>() {{
                put("canceled",1);
                put("flights",userFlights);
    		}};
    		stmt.close();
    		Map<String, Object> userinfo = new HashMap<String, Object>() {{
                put("cust",curUser);
                put("flights",userFlights);
    		}};
            ctx.render("/sub/customerinfo.vm",userinfo);
        });
        
        app.get("/adminflights", ctx -> {
            ctx.render("/sub/adminflights.vm");
        });
        
        app.post("/makenewflight", ctx -> {
			String start = ctx.formParam("start");
			String dest = ctx.formParam("destination");
			String date = ctx.formParam("departuredate");
			String time = ctx.formParam("departuretime");
			int seats = Integer.parseInt(ctx.formParam("seats"));
			int price = Integer.parseInt(ctx.formParam("price"));
			Statement stmt = con.createStatement();
			String insertquery = String.format("INSERT INTO Flight(time,destination_loc,departing_loc,date,total_seats,price) values('%s', '%s', '%s', '%s', %d, %d)",time,dest,start,date,seats,price);	
			stmt.executeUpdate(insertquery);
            ctx.render("/sub/adminflights.vm");
        });
        
        app.post("/adminviewflights", ctx -> {
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
			curUser = new User(fname, lname, contactn, ccnum, ccv, exp, cemail, pass, custid);
			String flightquery = String.format("SELECT * FROM Books B, Flight F WHERE B.customer_id = %d AND B.flight_id = F.flight_id", custid);
			rs = stmt.executeQuery(flightquery);
			while (rs.next()) {
				String s = rs.getString("departing_loc");
				String dest = rs.getString("destination_loc");
				String date = rs.getString("date");
				String time = rs.getString("time");
				int id = rs.getInt("flight_id");
				int sn = rs.getInt("seatn");
				int p = rs.getInt("price");
				Flight f = new Flight(s, dest, date, time, sn, p, id);
				userFlights.add(f);
			}
			Map<String, Object> userinfo = new HashMap<String, Object>() {{
                put("cust",curUser);
                put("flights",userFlights);
    		}};
            ctx.render("/sub/customerinfo.vm",userinfo);
        });
        
        /*
        app.post("/removeflight", ctx -> {
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
			curUser = new User(fname, lname, contactn, ccnum, ccv, exp, cemail, pass, custid);
			String flightquery = String.format("SELECT * FROM Books B, Flight F WHERE B.customer_id = %d AND B.flight_id = F.flight_id", custid);
			rs = stmt.executeQuery(flightquery);
			while (rs.next()) {
				String s = rs.getString("departing_loc");
				String dest = rs.getString("destination_loc");
				String date = rs.getString("date");
				String time = rs.getString("time");
				int id = rs.getInt("flight_id");
				int sn = rs.getInt("seatn");
				int p = rs.getInt("price");
				Flight f = new Flight(s, dest, date, time, sn, p, id);
				userFlights.add(f);
			}
			Map<String, Object> userinfo = new HashMap<String, Object>() {{
                put("cust",curUser);
                put("flights",userFlights);
    		}};
            ctx.render("/sub/customerinfo.vm",userinfo);
        });*/
        
    }
    
    private static int getHerokuAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
          return Integer.parseInt(herokuPort);
        }
        return 1000;
    }
    
    private static int getSeatNumber(int flight_id, Connection con) throws SQLException {
    	String getseat = String.format("SELECT * from Books WHERE flight_id = %d ORDER BY seatn",flight_id);
    	Statement stmt = con.createStatement();
    	ResultSet rs = stmt.executeQuery(getseat);
    	int emptyseat = 1;
    	while (rs.next()) {
    		int curseat = rs.getInt("seatn");
    		if (curseat != emptyseat) {
    			break;
    		}
    		emptyseat += 1;
    	}
    	rs.close();
		stmt.close();
    	return emptyseat;
    }
}