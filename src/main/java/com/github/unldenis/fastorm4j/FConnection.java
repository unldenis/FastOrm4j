package com.github.unldenis.fastorm4j;

import java.sql.*;


public class FConnection {

    private final String driver;
    private final String dbUrl;

    private Connection connection;

    public FConnection(String driver, String dbUrl) {
        this.driver = driver;
        this.dbUrl = dbUrl;
    }

    public void open() throws ClassNotFoundException, SQLException {
        // This will load the MySQL driver, each DB has its own driver
        Class.forName(driver);
        // Setup the connection with the DB
        connection = DriverManager.getConnection(dbUrl);
    }

    public void close() throws SQLException {
        if (connection != null)
            connection.close();
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

}