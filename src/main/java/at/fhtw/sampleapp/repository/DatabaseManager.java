package at.fhtw.sampleapp.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DatabaseManager {
    INSTANCE;

    public Connection getConnection()
    {
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mrpdb",
                    "mrpuser",
                    "mrpuser");
        } catch (SQLException e) {
            System.out.println("Fehler beim Verbindungsaufbau: " + e.getMessage());
            e.printStackTrace();

            throw new DataAccessException("Datenbankverbindungsaufbau nicht erfolgreich", e);
        }
    }
}
