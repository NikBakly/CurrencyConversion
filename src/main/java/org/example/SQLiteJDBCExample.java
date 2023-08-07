package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteJDBCExample {
    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Создание подключения к базе данных
            connection = DatabaseConnector.getConnection();

            if (connection != null) {
                System.out.println("Connected to the database");
                // Здесь вы можете выполнять операции с базой данных
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println("Connection closed");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
