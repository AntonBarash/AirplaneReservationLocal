import java.sql.Connection;
import java.sql.DriverManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class Flight {
    private String start;
    private String destination;
    private String date;
    private String time;
    private int seatnum;
    private int price;
    private int id;

    public Flight() {
        this.start = "";
        this.destination = "";
        this.date = "";
        this.time = "";
        this.seatnum = 0;
        this.price = 0;
        this.id = 0;
    }

    public Flight(String s, String dest, String d, String t, int sn, int p, int id) {
        this.start = s;
        this.destination = dest;
        this.date = d;
        this.time = t;
        this.seatnum = sn;
        this.price = p;
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public String getDest() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getSeat() {
        return seatnum;
    }

    public int getPrice() {
        return price;
    }
    
    public int getId() {
    	return id;
    }

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
    
    public boolean isValidSeat() {
    	return seatnum > 0;
    }
}
