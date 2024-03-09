import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class DataBase implements Closeable {
    private Connection _connection;
    private Statement _statement;

    public DataBase(String nameDB) throws SQLException {
        _connection = DriverManager.getConnection("jdbc:sqlite:" + nameDB);
        _statement = _connection.createStatement();

        _statement.execute("CREATE TABLE IF NOT EXISTS CrossRoad (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Direction TEXT, " +
                "Cars INTEGER, " +
                "Date_Time TEXT);");
    }

    @Override
    public void close() {
        try {
            _connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeData(ArrayList<Direction> directions) throws SQLException {
        var date_time = new GregorianCalendar().getTime();

        for (var direction : directions) {
            _statement.execute("INSERT INTO CrossRoad (Direction, Cars, Date_Time) VALUES " +
                    "('" + direction.getNameDirection() + "', " + direction.getCars() + ", '" + date_time + "');");
        }
    }
}
