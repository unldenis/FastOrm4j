# FastOrm4j

## What is FastOrm4j

FastOrm4j is a standalone library that favors the integration of software systems adhering to the object-oriented programming paradigm with RDBMS systems. This library was created for Java 17, the latest LTS.

Unlike the others, this one generates the classes in runtime, guaranteeing maximum performance.
In addition, error management is managed through the FResult, with a paradigm inspired by Rust.

## An example
```java
import com.github.unldenis.fastorm4j.*;
import com.github.unldenis.fastorm4j.ann.*;
import com.github.unldenis.fastorm4j.operation.*;
import com.github.unldenis.fastorm4j.result.*;

import java.sql.*;

public abstract class UserTester implements FListener {

    public static void main(String[] args) {
        var listener = FListener.Factory.newInstance(UserTester.class).unwrap();
        listener.insertUser(new Person("admin", 19));
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

public record Person(String name, int age) {
}
```
The example works, however it is possible to enable the debugging of the generated classes, in this case ...

```java
import com.github.unldenis.fastorm4j.*;
import com.github.unldenis.fastorm4j.result.*;
import java.sql.SQLException;

public final class UserTesterImpl extends UserTester {
    @Override
    public FIntResult<SQLException> insertUser(Person arg0) {
        String insertSQL = "INSERT INTO userTable2 (name, age) VALUES (?, ?)";
        FPreparedStatement stmt = connection().prepareStatement(insertSQL).unwrap();
        stmt.setString(1, arg0.name()).unwrap();
        stmt.setInt(2, arg0.age()).unwrap();
        return stmt.executeUpdate();
    }
}
```

## Warning
Until the first release it is not recommended to use it in production.
