package com.example.tgbotexchangeratess.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessagesRepository {
    private final ConnectionPool connectionPool;

    public MessagesRepository() { this.connectionPool = ConnectionPool.getConnectionPool(); }

    public void createMessages(String message) {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Messages (message) VALUES (?)");
        ) {
            preparedStatement.setString(1, message);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
