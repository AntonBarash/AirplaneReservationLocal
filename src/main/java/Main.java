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
    public static void main(String[] args) throws SQLException {
    	
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/sub", Location.CLASSPATH);}
        	//config.addStaticFiles(staticFiles -> {staticFiles.directory = "/";});}
        //).start(getHerokuAssignedPort()); //FOR HEROKU DEPLOYMENT
        ).start(1014); //FOR LOCAL TESTING: INCREASE PORT NUMBER EACH TEST, SINCE OLD ONE IS ALREADY TAKEN WHEN RAN
        
        //mysql connection
        Connection con=DriverManager.getConnection(  
        	"jdbc:mysql://localhost:3306/AirplaneRes","root","romepage");
        	
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
        		String exp = ctx.formParam("exp");
        		String pass = ctx.formParam("password");
        		curUser = new User(fname, lname, contactn, ccnum, ccv, exp, email, pass);
        		//stmt = con.createStatement(); PRETTY SURE I DONT NEED THIS LINE, ONE STATEMENT CREATION IS ENOUGH
        		String inputquery = String.format("INSERT INTO Customer(fname,lname,contactn,creditcardn,ccv,email,pass) values('%s','%s',%d,%d,%d,'%s','%s')", 
        				fname,lname,contactn,ccnum,ccv,email,pass);
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
            ctx.render("/sub/login.html");
        });
        
        app.post("/login", ctx -> {
        	//have SQL queries for checking login information, then check if admin or user account, depending on that, go to diff html page
            ctx.render("/sub/customer.html");
        });
        
        app.get("/logout", ctx -> {
        	curUser = new User();
        	Map<String, String> loggedin = new HashMap<String, String>() {{
                put("logout", "true");
            }};
            ctx.render("/sub/index.html",loggedin);
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