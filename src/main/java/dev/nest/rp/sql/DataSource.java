package dev.nest.rp.sql;

import com.zaxxer.hikari.HikariConfig;

public class DataSource {

    private final HikariConfig hikariConfig;

    public DataSource(AuthSource authSource) {
        this.hikariConfig = new HikariConfig();
        hikariConfig.setUsername(authSource.getAuthData().USERNAME());
        hikariConfig.setPassword(authSource.getAuthData().PASSWORD());
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s/%s", authSource.getAuthData().HOST(), authSource.getAuthData().DATABASE()));
        hikariConfig.setDriverClassName(authSource.getAuthData().DRIVER_CLASS_NAME());
        hikariConfig.setMaximumPoolSize((int) authSource.getAuthData().MAX_POOL_SIZE());
        hikariConfig.setMinimumIdle((int) authSource.getAuthData().MIN_IDLE());
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("serverName", authSource.getAuthData().HOST());
        hikariConfig.addDataSourceProperty("port", authSource.getAuthData().PORT());
        hikariConfig.addDataSourceProperty("databaseName", authSource.getAuthData().DATABASE());
    }

    public HikariConfig getHikariConfig() {
        return hikariConfig;
    }

}
