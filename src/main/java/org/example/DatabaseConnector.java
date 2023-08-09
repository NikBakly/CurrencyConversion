package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class DatabaseConnector {
    private static final String URL = "jdbc:sqlite:" +
            Objects.requireNonNull(DatabaseConnector.class.getResource("/database.db")).getPath();

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(URL);
    }
}
