import com.github.unldenis.fastorm4j.*;
import com.github.unldenis.fastorm4j.ann.*;
import com.github.unldenis.fastorm4j.operation.*;

import java.lang.reflect.*;
import java.sql.*;

public abstract class UserTester implements FListener {


    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var impl = FListener.compile(UserTester.class, "users", new Person("admin0", 19));

    }

    private final FConnection conn;

    public UserTester(String tableName, Person person) throws SQLException, ClassNotFoundException {
        conn = new FConnection("org.sqlite.JDBC", "jdbc:sqlite:sample.db");
        conn.open();

        if(FTable.builder(tableName, Person.class)
                .build(conn)
                .openTable()) {
            System.out.println("Table %s opened".formatted(tableName));
        }
        insertUser(person);
    }

    @Override
    public FConnection connection() {
        return conn;
    }

    @Operation(table = "users", type = OperationType.INSERT)
    public abstract int insertUser(Person person) throws SQLException;

    @Operation(table = "users", type = OperationType.INSERT)
    public void insertVariant(Person person) throws SQLException {

    }
}
