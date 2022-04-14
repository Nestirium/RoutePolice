package dev.nest.rp.sql;


import com.zaxxer.hikari.HikariDataSource;
import dev.nest.rp.cache.RouteCache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class DBManager {

    private final HikariDataSource hikariDataSource;

    public DBManager(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    public void createMandatoryTable() {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(Statements.CREATE_MANDATORY_TABLE.getStatement());
                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void createOptionalTable() {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(Statements.CREATE_OPTIONAL_TABLE.getStatement());
                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void cacheMandatoryRoutes(RouteCache routeCache) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(Statements.SELECT_MANDATORY_ROUTES.getStatement());
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = Integer.parseInt(set.getString(1));
                    String departure = set.getString(2);
                    String destination = set.getString(3);
                    String rvsm = set.getString(4);
                    String cnst = set.getString(5);
                    String route = set.getString(6);
                    RouteCache.Route routeObject = new RouteCache.Route(departure, destination, rvsm, cnst, route);
                    routeCache.cacheMandatory(id, routeObject);
                }
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void cacheOptionalRoutes(RouteCache routeCache) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(Statements.SELECT_OPTIONAL_ROUTES.getStatement());
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    int id = Integer.parseInt(set.getString(1));
                    String departure = set.getString(2);
                    String destination = set.getString(3);
                    String rvsm = set.getString(4);
                    String cnst = set.getString(5);
                    String route = set.getString(6);
                    RouteCache.Route routeObject = new RouteCache.Route(departure, destination, rvsm, cnst, route);
                    routeCache.cacheOptional(id, routeObject);
                }
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
