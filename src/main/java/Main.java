import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

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

public class Main {

    public static void main(String[] args) {
    	
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/sub", Location.CLASSPATH);}
        	//config.addStaticFiles(staticFiles -> {staticFiles.directory = "/";});}
        //).start(getHerokuAssignedPort());
        ).start(1004);
        
        app.post("/gotoregister", ctx -> {
            ctx.render("/sub/register.html");
        });
        
        app.post("/register", ctx -> {
        	//have SQL queries for inputting user data into database
            User curUser = new User(ctx.formParam("fname"), ctx.formParam("lname"), Integer.parseInt(ctx.formParam("contactn")), 
            		Integer.parseInt(ctx.formParam("creditcard")), Integer.parseInt(ctx.formParam("cvv")), ctx.formParam("exp"), ctx.formParam("email"), ctx.formParam("password"));
            //ctx.render("/sub/home.html");
            ctx.html(curUser.getFullName());
        });
        
        app.post("/gotologin", ctx -> {
            ctx.render("/sub/login.html");
        });
        
        app.post("/login", ctx -> {
        	//have SQL queries for checking login information
            ctx.render("/sub/home.html");
        });
        
        app.post("/logout", ctx -> {
        	//have SQL queries for checking login information
            ctx.render("/sub/home.html");
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