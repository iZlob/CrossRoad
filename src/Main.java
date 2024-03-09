import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws SQLException {
        ArrayList<Direction> directions = new ArrayList<>();
        ArrayList<Ride> rides = new ArrayList<>();
        ArrayList<Arrive> arrives = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            directions.add(new Direction(i));
            rides.add(new Ride(directions.get(i)));
            arrives.add(new Arrive(directions.get(i)));
        }

        for (int i = 0; i < 4; i++) {
            new Thread(rides.get(i)).start();
            new Thread(arrives.get(i)).start();
        }

        TrafficController controller = new TrafficController(directions);
        controller.startControl();

    }
}