import java.util.Calendar;
import java.util.GregorianCalendar;

public class Arrive implements Runnable {//поток котрый "прибавляет" машины на перекрестке когда горит красный
    private Direction _direction;

    public Arrive(Direction direction) {
        _direction = direction;
    }

    @Override
    public void run() {
        Calendar date = new GregorianCalendar();

        while (true) {
            int hour = date.getTime().getHours();

            if (hour >= 6 && hour < 24) {
                _direction.arrive();
            } else {
                _direction.stopWork();
            }
        }
    }
}
