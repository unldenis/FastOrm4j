# FastOrm4j

## What is FastOrm4j

FastOrm4j is a standalone library that favors the integration of software systems adhering to the object-oriented programming paradigm with RDBMS systems. This library was created for Java 17, the latest LTS.

Unlike the others, this one generates the classes in runtime, guaranteeing maximum performance.

## An example
The <a href="https://github.com/unldenis/FastOrm4j/blob/master/src/test/java/UserTester.java">example</a> works, however it is possible to enable the debugging of the generated classes, in this case ...

```java
import com.github.unldenis.fastorm4j.*;
import java.sql.*;

public final class UserTesterImpl extends UserTester {

    // Constructor
    public UserTesterImpl(java.lang.String arg0, Person arg1) throws java.sql.SQLException,java.lang.ClassNotFoundException {
        super(arg0, arg1);
    }

    // Operations
    @Override
    public void insertVariant(Person arg0) throws SQLException {
        String insertSQL = "INSERT INTO users (name, age) VALUES (?, ?)";
        PreparedStatement stmt = connection().prepareStatement(insertSQL);
        stmt.setString(1, arg0.name());
        stmt.setInt(2, arg0.age());
        stmt.executeUpdate();
    }

    @Override
    public int insertUser(Person arg0) throws SQLException {
        String insertSQL = "INSERT INTO users (name, age) VALUES (?, ?)";
        PreparedStatement stmt = connection().prepareStatement(insertSQL);
        stmt.setString(1, arg0.name());
        stmt.setInt(2, arg0.age());
        return stmt.executeUpdate();
    }
}
```

## Warning
Until the first release it is not recommended to use it in production.
