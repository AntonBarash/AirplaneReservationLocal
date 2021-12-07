import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class Flight {
	//class variables
    private String start;
    private String destination;
    private String date;
    private String time;
    private int seatnum;
    private int price;
    private int id;

    //default constructor for flight, for when flight is not created yet
    public Flight() {
        this.start = "";
        this.destination = "";
        this.date = "";
        this.time = "";
        this.seatnum = 0;
        this.price = 0;
        this.id = 0;
    }

    //constructor for flight with input, for when a flight has been created by the admin
    public Flight(String s, String dest, String d, String t, int sn, int p, int id) {
        this.start = s;
        this.destination = dest;
        this.date = d;
        this.time = t;
        this.seatnum = sn;
        this.price = p;
        this.id = id;
    }

    //getter method for retrieving a flights departure location
    public String getStart() {
        return start;
    }

    //getter method for retrieving a flights destination
    public String getDest() {
        return destination;
    }

    //getter method for retrieving a flights date of departure
    public String getDate() {
        return date;
    }

    //getter method for retrieving a flights time of departure
    public String getTime() {
        return time;
    }

    //getter method for retrieving a flights number of seats left	
    public int getSeat() {
        return seatnum;
    }

    //getter method for retrieving a flights price
    public int getPrice() {
        return price;
    }
    
    //getter method for retrieving a flights ID
    public int getId() {
    	return id;
    }

    //isvaliddate: instance method which returns a boolean, true if the flight is on or after the current date and time, and false if the flight is
    //in the past
    public boolean isValidDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

            Date dnow = sdf.parse(LocalDate.now().toString());
            Date d2 = sdf.parse(date);
            Date tnow = sdf2.parse(LocalTime.now().toString());
            Date t2 = sdf2.parse(time);

            if (dnow.before(d2) || (dnow.equals(d2) && tnow.before(t2)))
                return true;
            else
                return false;

        } catch (ParseException e) {
            return false;
        }
    }
    
    //isvalidseat: instance method which returns a boolean, true if there are available seats on the flight, false if there aren't
    public boolean isValidSeat() {
    	return seatnum > 0;
    }
}
