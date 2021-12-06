import io.javalin.Javalin;
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
    public static void main(String[] args) {
    	
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/sub", Location.CLASSPATH);}
        	//config.addStaticFiles(staticFiles -> {staticFiles.directory = "/";});}
        //).start(getHerokuAssignedPort()); //FOR HEROKU DEPLOYMENT
        ).start(1015); //FOR LOCAL TESTING: INCREASE PORT NUMBER EACH TEST, SINCE OLD ONE IS ALREADY TAKEN WHEN RAN
        
        
        //change all of these renders to the correct html page
        
        app.get("/register", ctx -> {
        	ctx.render("/sub/register.html");
        });
        
        app.post("/register", ctx -> {
        	//have SQL queries for inputting user data into database
            curUser = new User(ctx.formParam("fname"), ctx.formParam("lname"), Integer.parseInt(ctx.formParam("contactn")), 
            		Integer.parseInt(ctx.formParam("creditcard")), Integer.parseInt(ctx.formParam("cvv")), ctx.formParam("exp"), ctx.formParam("email"), ctx.formParam("password"));
            //ctx.render("/sub/home.html");
            ctx.html(curUser.getFullName());
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
        	try {
                //Class.forName("com.mysql.jdbc.Driver");  
                Connection con=DriverManager.getConnection(  
                		"jdbc:mysql://localhost:3306/test1","root","romepage"); 
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Actor");
                rs.next();
                System.out.println(rs.getInt("year_born"));
                loggedin2.put("yo1",rs);
                ctx.render("/sub/my-list.vm", loggedin2); 
            }
            catch (Exception e) {  
                    System.out.println(e.toString());  
            } 
                   	
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