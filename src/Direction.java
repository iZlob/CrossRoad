import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class Direction {
    private volatile int _cars;
    private volatile String _light;
    public static String redCode = "\u001B[31m";
    public static String yellowCode = "\u001B[33m";
    public static String greenCode = "\u001B[32m";
    public static String whiteCode = "\u001B[37m";
    private Calendar _date;
    private String _nameDirection;

    public Direction(int number) {
        _cars = 0;
        _light = "YellowBlink";
        _date = new GregorianCalendar();
        _nameDirection = "Direction-" + (number + 1);
    }

    public int getCars() {
        return _cars;
    }

    public String getLight() {
        return _light;
    }

    public String getNameDirection() {
        return _nameDirection;
    }

    public synchronized void arrive() {//тут машины приезжают
        if (_light == "Green") {
            notify();
        }

        while (_light == "Green") {
            Wait();
        }
        if (_light != "YellowBlink") {
            carsArrive();
            Delay(2000);
        }
    }

    public synchronized void ride() {//тут машины уезжают
        if (_light == "Red") {
            notify();
        }

        while (_light == "Red") {
            Wait();
        }

        if (_cars > 0 && _light == "Green") {
            _cars--;
            Delay(1000);
        } else if (_light == "Yellow") {
            carsArrive();
            Delay(2000);
        }
    }

    private void carsArrive() {//просто функция иммитирующая прибытие машин на перекресток в зависимости от времени суток
        int hour = _date.getTime().getHours();

        if ((hour >= 6 && hour < 10) || (hour >= 17 && hour < 20)) {//в часы пик больше машин
            _cars += new Random().nextInt(0, 3);
        } else if (hour >= 10 && hour < 17) {//днем поменьше машин
            _cars += new Random().nextInt(0, 2);
        } else if (hour >= 20 && hour < 24) {//вечером едут задержавшиеся на работе
            _cars += new Random().nextInt(0, 1);
        }
    }

    public void stopWork() {//останавливаем работу
        _cars = 0;
        _light = "YellowBlink";
    }

    public void switchRedLight() {//включаем красный свет
        _light = "Red";
        System.out.println(redCode + _nameDirection + ": Включен красный сигнал светофора!");
    }

    public void switchYellowLight() {//включаем желтый свет
        if (_light == "Red") {
            System.out.println(yellowCode + _nameDirection + ": Включен желтый сигнал светофора! А так же дополнительно горит красный!");
        } else if (_light == "Green") {
            System.out.println(yellowCode + _nameDirection + ": Включен желтый сигнал светофора! А так же дополнительно горит зеленый!");
        }

        _light = "Yellow";
    }

    public void switchGreenLight() {//включаем зеленый свет
        _light = "Green";
        System.out.println(greenCode + _nameDirection + ": Включен зеленый сигнал светофора!");
    }

    public void switchYellowBlinkLight() {//включаем режим ожидания светофора
        _light = "YellowBlink";
        System.out.println(yellowCode + _nameDirection + ": Светофор в режиме ожидания. Мигает желтый свет!");
    }

    private void Wait() {
        try {
            wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void Delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int compare(Direction direction1, Direction direction2) {
        if (direction1._cars > direction2._cars)
            return 1;
        return -1;
    }
}
