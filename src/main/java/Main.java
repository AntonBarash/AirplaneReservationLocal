import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
public class Main {
	static User curUser = new User(); //user object for user login
	static Admin curAdmin = new Admin(); //admin object for admin login
	static ArrayList<Flight> userFlights = new ArrayList<Flight>(); //arraylist of all the flights the user is booked on
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
    	
    	//initialization of javalin app
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/sub", Location.CLASSPATH);}
        ).start(getHerokuAssignedPort()); //FOR HEROKU DEPLOYMENT
        //).start(1000); //FOR LOCAL TESTING: INCREASE PORT NUMBER EACH TEST, SINCE OLD ONE IS ALREADY TAKEN WHEN RAN
        
        //MYSQL CONNECTIONS:
        
        //LOCALHOST CONNECTION, CHANGE "user" and "pass" TO LOCAL MYSQL USER and PASS:
        //Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AirplaneRes","root","romepage"); //LOCALHOST CONNECTION  
        
        
        //HEROKU CLEARDB CONNECTION, IS SLOW AND CAN CRASH (UNCOMMENT BOTH OF NEXT LINES):
        //String dbUrl = "jdbc:mysql://us-cdbr-east-04.cleardb.com/heroku_50d2532af7614cd?password=1a653a5a&reconnect=true&user=b6e662acb93d8f";
        //Connection con = DriverManager.getConnection(dbUrl);
        
        //AWS CONNECTION, FASTEST CONNECTION AND HOSTED REMOTELY, ONLY FAILS IF AWS GOES DOWN FOR SOME REASON:
        String dbUrl = "jdbc:mysql://database-1.cdwkgehsrjdn.us-east-2.rds.amazonaws.com:3306/awsap?user=admin&password=password";
        Connection con = DriverManager.getConnection(dbUrl);
            	
    	//get action for register: displays the register page
        app.get("/register", ctx -> {
        	ctx.render("/sub/register.vm");
        });
        
        //post action for register: creates an account, using the input in the register form
        app.post("/register", ctx -> {
        	String email = ctx.formParam("email");
        	//Connection con = DriverManager.getConnection(dbUrl);
        	Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer WHERE email = '" + email + "'");
        	if (rs.next()) { //if account with this email has already been made
        		Map<String, Integer> alreadyusedemail = new HashMap<String, Integer>() {{
                    put("usedemail", 1);
        		}};
        		rs.close();
        		stmt.close();
        		//con.close();
        		ctx.render("/sub/register.vm",alreadyusedemail);
        	}
        	else {
        		String fname = ctx.formParam("fname");
        		String lname = ctx.formParam("lname");
        		String contactn = ctx.formParam("contactn");
        		String ccnum = ctx.formParam("creditcard");
        		int ccv = Integer.parseInt(ctx.formParam("cvv"));
        		int exp = Integer.parseInt(ctx.formParam("exp"));
        		String pass = ctx.formParam("password");
        		String inputquery = String.format("INSERT INTO Customer(fname,lname,contactn,creditcardn,ccv,exp,email,pass,account_type) values('%s','%s','%s','%s',%d,%d,'%s','%s',%d)", 
                				fname,lname,contactn,ccnum,ccv,exp,email,pass,0);
                stmt.executeUpdate(inputquery);
                rs = stmt.executeQuery("SELECT customer_id FROM Customer WHERE email = '" + email + "'");
                rs.next();
                int id = rs.getInt("customer_id");
        		curUser = new User(fname, lname, contactn, ccnum, ccv, exp, email, pass, id);
        		rs.close();
        		stmt.close();
        		//con.close();
        		ctx.render("/sub/customer.html");
        	} 
        });

        //get action for login: goes to login page
        app.get("/login", ctx -> {
        	//Connection con = DriverManager.getConnection(dbUrl);
        	//con.close();
            ctx.render("/sub/login.vm");
        });
        
        
        //post action for login: logs in based on email and password, or outputs message about wrong login info
        app.post("/login", ctx -> {
        	String email = ctx.formParam("email");
        	//Connection con = DriverManager.getConnection(dbUrl);
        	Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer WHERE email = '" + email + "'");
            if (rs.next()) { //if this is true, that means there is an account with this email, so you check password
            	String pass = ctx.formParam("password");
            	String correctpass = rs.getString("pass");
            	if (pass.equals(correctpass)) { //if this is true, that means the password is correct for that email
            		int accounttype = rs.getInt("account_type");
            		if (accounttype == 0) { //if account type is 0, you go to user page since 0 is for user accounts
            			String fname = rs.getString("fname");
            			String lname = rs.getString("lname");
            			String contactn = rs.getString("contactn");
            			String ccnum = rs.getString("creditcardn");
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
                		//con.close();
            			ctx.render("/sub/customer.html");
            		}
            		else { //if accounttype is 1, you go to admin page since 1 is for admin accounts
            			String fname = rs.getString("fname");
            			String lname = rs.getString("lname");
            			Admin curAdmin = new Admin(email,pass);
            			rs.close();
                		stmt.close();
                		//con.close();
            			ctx.render("/sub/admin.html");
            		}
            	}
            	else { //wrong password, so you output wrong password message
            		Map<String, Integer> nothispass = new HashMap<String, Integer>() {{
                        put("wrongpass", 1);
            		}};
            		rs.close();
            		stmt.close();
            		//con.close();
            		ctx.render("/sub/login.vm",nothispass);
            	}
        	}
        	else { //no account with input email, so you output wrong email message
        		Map<String, Integer> nothisemail = new HashMap<String, Integer>() {{
                    put("wrongemail", 1);
        		}};
        		rs.close();
        		stmt.close();
        		//con.close();
        		ctx.render("/sub/login.vm",nothisemail);
        	}
        });
        
        //get action for logout: resets user, admin, and userflights objects because now nobody is logged in, and goes to home page (index.html)
        app.get("/logout", ctx -> {
        	curUser = new User();
        	curAdmin = new Admin();
        	userFlights = new ArrayList<Flight>();
            ctx.render("/sub/index.html");
        });
        
        //get action for searchflights: goes to searchflights page
        app.get("/searchflights", ctx -> {
            ctx.render("/sub/searchflights.vm");
        });
        
       //post action for searchflights: based on user input of destination, show all flights with that destination
        app.post("/searchflights", ctx -> {
        	//Connection con = DriverManager.getConnection(dbUrl);
        	ArrayList<Flight> flights = User.viewFlights(ctx,con);
        	Map<String, ArrayList<Flight>> allflights = new HashMap<String, ArrayList<Flight>>() {{
                put("flights",flights);
    		}};
    		//con.close();
            ctx.render("/sub/searchflights.vm",allflights);
        });
        
        //get action for customer: goes to customer page, which is the default user page
        app.get("/customer", ctx -> {
            ctx.render("/sub/customer.html");
        });
        
        //get action for admin: goes to the admin page, which is the default admin page, and also reinitializes curUser and userFlights, since
        //the logged in person is now an admin, not a user
        app.get("/admin", ctx -> {
        	curUser = new User();
        	userFlights = new ArrayList<Flight>();
            ctx.render("/sub/admin.html");
        });
        
        //get action for customerflights: outputs all of the currently logged in user's flights, using the userflights arraylist
        //each flight has a button next to it to cancel it, if the user wants to
        app.get("/customerflights", ctx -> {
        	Map<String, ArrayList<Flight>> booked = new HashMap<String, ArrayList<Flight>>() {{
                put("flights",userFlights);
    		}};
            ctx.render("/sub/customerflights.vm",booked);
        });
        
        //post action for bookflight: books a flight that the user has picked, adding it to the user's userflight list
        app.post("/bookflight", ctx -> {
        	//Connection con = DriverManager.getConnection(dbUrl);
        	Flight f = User.bookFlight(ctx, con, curUser);
        	userFlights.add(f);
        	Map<String, Integer> booked = new HashMap<String, Integer>() {{
                put("booked",1);
    		}};
    		//con.close();
            ctx.render("/sub/searchflights.vm",booked);
        });
        
        //post action for customercancel: cancels a flight the user has chosen to cancel, removing it from their userflight list
        app.post("/customercancel", ctx -> {
        	//Connection con = DriverManager.getConnection(dbUrl);
        	int index = User.cancelFlight(ctx, con, curUser);
        	userFlights.remove(index);
        	Map<String, Object> canceled = new HashMap<String, Object>() {{
                put("canceled",1);
                put("flights",userFlights);
    		}};
    		//con.close();
            ctx.render("/sub/customerflights.vm",canceled);
        });
        
        //get action for customerinfo: goes to customer info page
        app.get("/customerinfo", ctx -> {
            ctx.render("/sub/customerinfo.vm");
        });
        
        //post action for searchcustomer: shows all information about the user and their flights that the admin has searched for
        app.post("/searchcustomer", ctx -> {
        	//Connection con = DriverManager.getConnection(dbUrl);
        	curUser = Admin.searchCustomer(ctx, con);
        	userFlights = Admin.searchCustomerFlights(ctx, con, curUser);
			Map<String, Object> userinfo = new HashMap<String, Object>() {{
                put("cust",curUser);
                put("flights",userFlights);
    		}};
    		//con.close();
            ctx.render("/sub/customerinfo.vm",userinfo);
        });
        
        //post action for admincancel: cancels a flight for a specific user
        app.post("/admincancel", ctx -> {
        	//Connection con = DriverManager.getConnection(dbUrl);
        	int index = Admin.cancelFlight(ctx, con, curUser);
        	userFlights.remove(index);    		
    		Map<String, Object> userinfo = new HashMap<String, Object>() {{
                put("cust",curUser);
                put("flights",userFlights);
                put("canceled",1);
    		}};
    		//con.close();
            ctx.render("/sub/customerinfo.vm",userinfo);
        });
        
        //get action for adminflights: goes to adminflights page, where admin can make new flights and remove existing ones
        app.get("/adminflights", ctx -> {
            ctx.render("/sub/adminflights.vm");
        });
        
        //post action for makenewflight: makes a new flight with the information the admin has input into the form
        app.post("/makenewflight", ctx -> {
        	//Connection con = DriverManager.getConnection(dbUrl);
			Admin.addFlight(ctx, con);
			//con.close();
            ctx.render("/sub/adminflights.vm");
        });
        
        //post action for adminviewflights: lets the admin view all of the flights for the destination that they put in
        app.post("/adminviewflights", ctx -> {
        	//Connection con = DriverManager.getConnection(dbUrl);
        	ArrayList<Flight> flights = Admin.viewFlights(ctx, con);
        	Map<String, ArrayList<Flight>> allflights = new HashMap<String, ArrayList<Flight>>() {{
                put("flights",flights);
    		}};
    		//con.close();
            ctx.render("/sub/adminflights.vm",allflights);
        });
        
        //post action for removeflight: removes the flight the admin has selected to remove
        app.post("/removeflight", ctx -> {
        	//Connection con = DriverManager.getConnection(dbUrl);
        	Admin.removeFlight(ctx,con);
        	Map<String, Integer> removed = new HashMap<String, Integer>() {{
                put("removed",1);
    		}};
    		//con.close();
            ctx.render("/sub/adminflights.vm",removed);
        });
        
    }
    
    //gets the correct port for the heroku deployment
    private static int getHerokuAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
          return Integer.parseInt(herokuPort);
        }
        return 1000;
    }
    
    //getSeatNumber: returns the next available seat number in a flight, looking at the lowest possible empty one (if 1,3,4 are taken, will give 2)
    public static int getSeatNumber(int flight_id, Connection con) throws SQLException {
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