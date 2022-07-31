package com.github.unldenis.fastorm4j;

import com.github.unldenis.fastorm4j.result.*;
import com.github.unldenis.fastorm4j.util.Void;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.github.unldenis.fastorm4j.result.FResult.*;

public class FConnection {

    private final String driver;
    private final String dbUrl;

    private Connection connection;

    public FConnection(String driver, String dbUrl) {
        this.driver = driver;
        this.dbUrl = dbUrl;
    }

    public FResult<Void, Exception> open() {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName(driver);
            // Setup the connection with the DB
            connection = DriverManager.getConnection(dbUrl);

            return VOID_EXCEPTION;
        } catch (ClassNotFoundException | SQLException e) {
            return err(e);
        }
    }

    public FResult<Void, SQLException> close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                return err(e);
            }
        }
        return VOID_SQL_EXCEPTION;
    }

    public FResult<FStatement, SQLException> createStatement() {
        try {
            return ok(FStatement.of(connection.createStatement()));
        } catch (SQLException e) {
            return err(e);
        }
    }

    public FResult<FPreparedStatement, SQLException> prepareStatement(String sql) {
        try {
            return ok(FPreparedStatement.of(connection.prepareStatement(sql)));
        } catch (SQLException e) {
            return err(e);
        }
    }

}