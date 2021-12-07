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
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
    	
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/sub", Location.CLASSPATH);}
        	//config.addStaticFiles(staticFiles -> {staticFiles.directory = "/";});}
        //).start(getHerokuAssignedPort()); //FOR HEROKU DEPLOYMENT
        ).start(1011); //FOR LOCAL TESTING: INCREASE PORT NUMBER EACH TEST, SINCE OLD ONE IS ALREADY TAKEN WHEN RAN
        
        //mysql connection
        //Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://us-cdbr-east-04.cleardb.com/heroku_50d2532af7614cd?password=1a653a5a&reconnect=true&user=b6e662acb93d8f";
        //Connection con = DriverManager.getConnection(dbUrl, "b6e662acb93d8f", "1a653a5a");
        Connection con = DriverManager.getConnection(dbUrl);
        Statement stmt1 = con.createStatement();
        ResultSet rs1 = stmt1.executeQuery("SELECT 1");
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
        		//String exp = ctx.formParam("exp");
        		String pass = ctx.formParam("password");
        		curUser = new User(fname, lname, contactn, ccnum, ccv, email, pass);
        		//stmt = con.createStatement(); PRETTY SURE I DONT NEED THIS LINE, ONE STATEMENT CREATION IS ENOUGH
        		String inputquery = String.format("INSERT INTO Customer(fname,lname,contactn,creditcardn,ccv,email,pass,account_type) values('%s','%s',%d,%d,%d,'%s','%s',%d)", 
        				fname,lname,contactn,ccnum,ccv,email,pass,0);
        		stmt.executeUpdate(inputquery);
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
            			curUser = new User(fname, lname, contactn, ccnum, ccv, email, pass);
            			ctx.render("/sub/customer.html");
            		}
            		else {
            			String fname = rs.getString("fname");
            			String lname = rs.getString("lname");
            			Admin curAdmin = new Admin(email,pass);
            			ctx.render("/sub/admin.html");
            		}
            	}
            	else {
            		Map<String, Integer> nothispass = new HashMap<String, Integer>() {{
                        put("wrongpass", 1);
            		}};
            		ctx.render("/sub/login.vm",nothispass);
            	}
        	}
        	else {
        		Map<String, Integer> nothisemail = new HashMap<String, Integer>() {{
                    put("wrongemail", 1);
        		}};
        		ctx.render("/sub/login.vm",nothisemail);
        	}
        });
        
        app.get("/logout", ctx -> {
        	curUser = new User();
            ctx.render("/sub/index.html");
        });
        
        app.get("/flightinfo", ctx -> {
            ctx.render("/sub/index.html");
        });
        
        app.get("/customerinfo", ctx -> {
            ctx.render("/sub/customer.html");
        });
    }
    
    private static int getHerokuAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
          return Integer.parseInt(herokuPort);
        }
        return 1000;
    }
    
    

}