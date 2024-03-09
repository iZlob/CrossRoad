import java.util.Calendar;
import java.util.GregorianCalendar;

public class Ride implements Runnable {//поток котрый "выгоняет" машины с перекрестка когда горит зеленый
    private Direction _direction;

    public Ride(Direction direction) {
        _direction = direction;
    }

    @Override
    public void run() {
        Calendar date = new GregorianCalendar();

        while (true) {
            int hour = date.getTime().getHours();

            if (hour >= 6 && hour < 24) {
                _direction.ride();
            } else {
                _direction.stopWork();
            }
        }
    }
}
