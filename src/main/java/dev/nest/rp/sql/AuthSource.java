package dev.nest.rp.sql;


import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AuthSource {

    private final String DIR_NAME = "Route Police";
    private final Path DIR = Paths.get("C:\\Users\\ahman\\Desktop\\" + DIR_NAME);
    private final String AUTH_FILE_NAME = "mysql.json";
    private final Path AUTH_FILE = Paths.get(DIR.toAbsolutePath() + "\\" + AUTH_FILE_NAME);
    private AuthData authData;

    public AuthSource() throws IOException {
        if (!Files.isDirectory(DIR)) {
            Files.createDirectory(DIR);
            Files.createFile(AUTH_FILE);
            writeDefaults();
            readData();
            return;
        }
        if (!Files.exists(AUTH_FILE)) {
            Files.createFile(AUTH_FILE);
            writeDefaults();
            readData();
            return;
        }
        readData();
    }

    @SuppressWarnings("unchecked")
    private void writeDefaults() throws IOException {
        JSONObject defSqlDets = new JSONObject();
        defSqlDets.put("username", "root");
        defSqlDets.put("password", "root");
        defSqlDets.put("database", "route_db");
        defSqlDets.put("driver-class-name", "com.mysql.jdbc.Driver");
        defSqlDets.put("max-pool-size", 3);
        defSqlDets.put("min-idle", 1);
        defSqlDets.put("host", "127.0.0.1");
        defSqlDets.put("port", 3306);
        defSqlDets.put("bot-token", "");
        BufferedWriter writer = new BufferedWriter(new FileWriter(AUTH_FILE.toFile()));
        defSqlDets.writeJSONString(writer);
        writer.close();
    }

    private void readData() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(AUTH_FILE.toFile()));
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(reader);
            JSONObject sqlData = (JSONObject) obj;
            String username = (String) sqlData.get("username");
            String password = (String) sqlData.get("password");
            String database = (String) sqlData.get("database");
            String driverClassName = (String) sqlData.get("driver-class-name");
            long maxPoolSize = (long) sqlData.get("max-pool-size");
            long minIdle = (long) sqlData.get("min-idle");
            String host = (String) sqlData.get("host");
            long port = (long) sqlData.get("port");
            String botToken = (String) sqlData.get("bot-token");
            this.authData = new AuthData(username, password, database, driverClassName, maxPoolSize, minIdle, host, port, botToken);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public AuthData getAuthData() {
        return authData;
    }

    public record AuthData(String USERNAME,
                           String PASSWORD,
                           String DATABASE,
                           String DRIVER_CLASS_NAME,
                           long MAX_POOL_SIZE,
                           long MIN_IDLE,
                           String HOST,
                           long PORT, String BOT_TOKEN) {
    }


}
