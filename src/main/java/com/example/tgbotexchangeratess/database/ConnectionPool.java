package com.example.tgbotexchangeratess.database;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {
    private final Properties properties = initializeProperties();
    private final String URL = properties.getProperty("url");
    private final String USERNAME = properties.getProperty("username");
    private final String PASSWORD = properties.getProperty("password");
    private final int MAX_CONNECTIONS = Integer.parseInt(properties.getProperty("max_connections"));
    private static ConnectionPool instance = null;

    private final BlockingQueue<Connection> blockingQueue = new ArrayBlockingQueue<>(MAX_CONNECTIONS);

    private Properties initializeProperties() {
        Properties properties = new Properties();
        try {
            FileReader reader =
                    new FileReader("src/main/resources/database.properties");
            properties.load(reader);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ConnectionPool() {
        try {
            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                blockingQueue.put(createConnection());
            }
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConnectionPool getConnectionPool() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public synchronized Connection getConnection() {
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}




