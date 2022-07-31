import com.github.unldenis.fastorm4j.*;
import com.github.unldenis.fastorm4j.ann.*;
import com.github.unldenis.fastorm4j.operation.*;
import com.github.unldenis.fastorm4j.result.*;

import java.sql.*;

public abstract class UserTester implements FListener {

    public static void main(String[] args) {
        var listener = FListener.Factory.newInstance(UserTester.class).unwrap();
        int code = listener.insertUser(new Person("admin", 19)).unwrap();
    }

    private final FConnection conn;

    public UserTester() {
        conn = new FConnection("org.sqlite.JDBC", "jdbc:sqlite:sample.db");
        conn.open().expect("Connection not open");

        FTable.builder("userTable3", Person.class)
            .unwrap()
            .build(conn)
            .unwrap()
            .openTable()
            .err().ifPresent(Throwable::printStackTrace);
    }

    @Override
    public FConnection connection() {
        return conn;
    }

    @Operation(table = "userTable2", type = OperationType.INSERT)
    public abstract FIntResult<SQLException> insertUser(Person person);
}
