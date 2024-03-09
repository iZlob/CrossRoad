import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TrafficController {
    private ArrayList<Direction> _directions;
    private int _greenTimeStart;//in sec
    private int _repeatOneTime;
    private DataBase _db;

    public TrafficController(ArrayList<Direction> directions) throws SQLException {
        _directions = directions;
        _greenTimeStart = 0;
        _repeatOneTime = 0;
        _db = new DataBase("CrossRoadDB.sqlite");
    }

    public void startControl() throws SQLException {
        Calendar date = new GregorianCalendar();
        int indexDirection = 0;


        while (true) {
            int hour = date.getTime().getHours();

            if (isAllYellowBlinkLights() && hour >= 6 && hour < 24) {//если все светофоры в режиме ожидания и кончилась ночь включаем светофоры
                for (var direction : _directions) {
                    direction.switchRedLight();
                }

                System.out.println("Ждем пока соберется народ!");
                _repeatOneTime = 0;
                Delay(5000);
            }

            if (isAllRedLights()) {//если светофоры все горят красными
                getStatistic();//выводим статистику по собравшимся машинам по каждому направлению

                _db.writeData(_directions);//и записываем ее в БД

                Direction mainDirection = _directions.stream().max(Direction::compare).get();//находим главное направление - то где больше скопилось машин
                indexDirection = _directions.indexOf(mainDirection);//находим его индекс

                switch (indexDirection) {//и включаем главному птоку и его противоположной стороне зеленый свет
                    case 0: {
                        switchOnGreenLight(indexDirection, indexDirection + 2);
                        _greenTimeStart = date.getTime().getSeconds();//фиксируем момент включения чтоб потом не дать гореть зеленому больше положенного
                        break;
                    }
                    case 1: {
                        switchOnGreenLight(indexDirection, indexDirection + 2);
                        _greenTimeStart = date.getTime().getSeconds();
                        break;
                    }
                    case 2: {
                        switchOnGreenLight(indexDirection, indexDirection - 2);
                        _greenTimeStart = date.getTime().getSeconds();
                        break;
                    }
                    case 3: {
                        switchOnGreenLight(indexDirection, indexDirection - 2);
                        _greenTimeStart = date.getTime().getSeconds();
                        break;
                    }
                    default:
                        break;
                }
            } else if (isSwitchLimit() && (date.getTime().getSeconds() - _greenTimeStart) <= 60) {//если машин стало меньши по заданным условиям или проло 60 сек с момента включения
                switch (indexDirection) {//включаем зеленому птоку и его противоположной стороне красный свет
                    case 0: {
                        switchOnRedLight(indexDirection, indexDirection + 2);
                        break;
                    }
                    case 1: {
                        switchOnRedLight(indexDirection, indexDirection + 2);
                        break;
                    }
                    case 2: {
                        switchOnRedLight(indexDirection, indexDirection - 2);
                        break;
                    }
                    case 3: {
                        switchOnRedLight(indexDirection, indexDirection - 2);
                        break;
                    }
                    default:
                        break;
                }
            }

            if (hour < 6) {//если наступила ночь включаем светофоры в режим ожидания
                if (_repeatOneTime < 1) {
                    for (var direction : _directions) {
                        direction.switchYellowBlinkLight();
                    }

                    _repeatOneTime++;
                }
            }
        }
    }

    private void switchOnGreenLight(int index1, int index2) {//функция вкл зеленого света
        _directions.get(index1).switchYellowLight();
        _directions.get(index2).switchYellowLight();
        Delay(2000);
        _directions.get(index1).switchGreenLight();
        _directions.get(index2).switchGreenLight();
    }

    private void switchOnRedLight(int index1, int index2) {//функция вкл красного света
        _directions.get(index1).switchYellowLight();
        _directions.get(index2).switchYellowLight();
        Delay(2000);
        _directions.get(index1).switchRedLight();
        _directions.get(index2).switchRedLight();
    }

    private boolean isAllYellowBlinkLights() {//проверка режима ожидания светофоров
        return _directions.get(0).getLight() == "YellowBlink" && _directions.get(1).getLight() == "YellowBlink" &&
                _directions.get(2).getLight() == "YellowBlink" && _directions.get(3).getLight() == "YellowBlink";
    }

    private boolean isAllRedLights() {//проверка все ли красные светофоры
        return _directions.get(0).getLight() == "Red" && _directions.get(1).getLight() == "Red" &&
                _directions.get(2).getLight() == "Red" && _directions.get(3).getLight() == "Red";
    }

    private boolean isSwitchLimit() {//условия переключения светофоров
        boolean ok = false;

        if (_directions.get(0).getLight() == "Green" && _directions.get(2).getLight() == "Green") {
            if ((_directions.get(1).getCars() + _directions.get(3).getCars()) >= 1.5 * (_directions.get(0).getCars() + _directions.get(2).getCars())) {
                ok = true;
            }
        } else if (_directions.get(1).getLight() == "Green" && _directions.get(3).getLight() == "Green") {
            if ((_directions.get(0).getCars() + _directions.get(2).getCars()) >= 1.5 * (_directions.get(1).getCars() + _directions.get(3).getCars())) {
                ok = true;
            }
        }

        return ok;
    }

    private void getStatistic() {//статистика
        System.out.println(Direction.whiteCode + "Статистика:");
        for (int i = 0; i < 4; i++) {
            System.out.println(_directions.get(i).getNameDirection() + ": собралось " + _directions.get(i).getCars() + " машин");
        }
    }

    private void Delay(int ms) {//задержа времени
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
