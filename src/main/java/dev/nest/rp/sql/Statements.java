package dev.nest.rp.sql;

public enum Statements {

    CREATE_MANDATORY_TABLE(
            "CREATE TABLE IF NOT EXISTS " +
                    "routes_mandatory" +
                    "(id INT NOT NULL AUTO_INCREMENT, " +
                    "dep VARCHAR(4) NOT NULL, " +
                    "dest VARCHAR(4) NOT NULL, " +
                    "rvsm VARCHAR(4) NOT NULL, " +
                    "cnst VARCHAR(16), " +
                    "route TEXT NOT NULL, " +
                    "PRIMARY KEY(id))"
    ),
    CREATE_OPTIONAL_TABLE(
            "CREATE TABLE IF NOT EXISTS " +
                    "routes_optional" +
                    "(id INT NOT NULL AUTO_INCREMENT, " +
                    "dep VARCHAR(4) NOT NULL, " +
                    "dest VARCHAR(4) NOT NULL, " +
                    "rvsm VARCHAR(4) NOT NULL, " +
                    "cnst VARCHAR(16), " +
                    "route TEXT NOT NULL, " +
                    "PRIMARY KEY(id))"
    ),
    SELECT_MANDATORY_ROUTES("SELECT * FROM routes_mandatory"),
    SELECT_OPTIONAL_ROUTES("SELECT * FROM routes_optional");

    private final String statement;

    Statements(String statement) {
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

}
