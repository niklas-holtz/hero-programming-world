package controller.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBManager {

    private static final String DB_NAME = "examples";

    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private Connection connection;

    public DBManager() {
        this.createDBIfNecessary();
    }

    public synchronized void deleteExample(int id) {
        Statement stmt = null;
        ResultSet res = null;
        try {
            if (this.connection == null && !this.connect(false)) {
                return;
            }

            stmt = this.connection.createStatement();
            res = stmt.executeQuery("SELECT codeID FROM examples WHERE exampleID = " + id);
            String codeID = new String();
            if (res.next()) {
                codeID = res.getString(1);
            }

            stmt.executeUpdate("DELETE FROM examples WHERE exampleID = " + id);
            if (codeID != null && !codeID.isEmpty())
                stmt.executeUpdate("DELETE FROM codes WHERE codeID = " + codeID);

        } catch (SQLException e) {
            try {
                this.connection.close();
            } catch (SQLException ex) {
            }
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }

    }

    public synchronized String getExampleMap(int id) {
        Statement stmt = null;
        ResultSet res = null;
        String path = null;
        try {
            if (this.connection == null && !this.connect(false)) {
                return null;
            }
            stmt = this.connection.createStatement();
            res = stmt.executeQuery("SELECT mapPath FROM examples WHERE exampleID = " + id);
            if (res.next()) {
                path = res.getString(1);
            }

        } catch (SQLException e) {
            try {
                this.connection.close();
            } catch (SQLException ex) {
            }
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
        return path;
    }

    public synchronized String getExampleCode(int id) {
        Statement stmt = null;
        ResultSet res = null;
        String code = null;
        try {
            if (this.connection == null && !this.connect(false)) {
                return null;
            }
            stmt = this.connection.createStatement();
            res = stmt.executeQuery("SELECT c.code FROM codes c, examples e WHERE e.codeID = c.codeID AND e.exampleID = " + id);
            if (res.next()) {
                code = res.getString(1);
            }
        } catch (SQLException e) {
            try {
                this.connection.close();
            } catch (SQLException ex) {
            }
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
        return code;
    }

    public synchronized String[][] getAllExamplesAsArray() {
        Statement stmt = null;
        ResultSet res = null;
        String[][] examples = null;
        try {
            if (this.connection == null && !this.connect(false)) {
                return null;
            }
            stmt = this.connection.createStatement();
            res = stmt.executeQuery("SELECT name, tags, mapPath, exampleID FROM examples");
            examples = new String[this.getExamplesCount()][4];
            int rowcounter = 0;
            while (res.next()) {
                for (int i = 1; i <= 4; i++) {
                    examples[rowcounter][i - 1] = res.getString(i);
                }
                rowcounter++;
            }
        } catch (SQLException e) {
            try {
                this.connection.close();
            } catch (SQLException ex) {
            }
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
        return examples;
    }

    public synchronized int getExamplesCount() {
        Statement stmt = null;
        ResultSet res = null;
        int count = -1;
        try {
            if (this.connection == null && !this.connect(false)) {
                return 0;
            }
            stmt = this.connection.createStatement();
            res = stmt.executeQuery("SELECT COUNT(*) FROM examples");
            if (res.next())
                count = res.getInt(1);
        } catch (SQLException e) {
            try {
                this.connection.close();
            } catch (SQLException ex) {
            }
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
        return count;
    }

    @SuppressWarnings("resource")
    public void saveExample(String tags, File map, String code) {
        PreparedStatement p = null;
        try {
            if (this.connection == null && !this.connect(false)) {
                return;
            }
            String codeID = this.getNextID("codes");
            String examplesID = this.getNextID("examples");
            String sql = "INSERT INTO codes VALUES (?, ?)";
            p = this.connection.prepareStatement(sql);
            p.setString(1, codeID);
            p.setString(2, code);
            p.executeUpdate();
            sql = "INSERT INTO examples VALUES (?, ?, ?, ?, ?)";
            p = this.connection.prepareStatement(sql);
            p.setString(1, examplesID);
            p.setString(2, map.getName().replaceFirst("[.][^.]+$", ""));
            p.setString(3, map.getPath());
            p.setString(4, codeID);
            p.setString(5, tags);
            p.executeUpdate();
            //	printAllData();
        } catch (SQLException e) {
            try {
                this.connection.close();
            } catch (SQLException ex) {
            }
        } finally {
            if (p != null) {
                try {
                    p.close();
                } catch (SQLException e) {
                }
            }

        }
    }

    private synchronized String getNextID(String tableName) {
        String minReturn = "1";
        Statement stmt = null;
        ResultSet res = null;
        try {
            if (this.connection == null && !this.connect(false)) {
                return null;
            }
            stmt = this.connection.createStatement();
            res = stmt.executeQuery("SELECT MAX(codeID) FROM codes");
            if (res.next()) {
                minReturn = Integer.toString(res.getInt(1) + 1);
            }
        } catch (SQLException e) {
            try {
                this.connection.close();
            } catch (SQLException ex) {
            }
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
        return minReturn;
    }

    private void createDBIfNecessary() {
        if (!new File(DBManager.DB_NAME).exists()) {
            //if file does not exist create a new one
            if (this.connect(true)) {
                //System.out.println("Creating new database..");
                try (Statement stmt = this.connection.createStatement()) {
                    stmt.execute("CREATE TABLE codes (codeID int not null, code LONG VARCHAR, PRIMARY KEY(codeID))");
                    stmt.execute("CREATE TABLE examples (exampleID int not null, name VARCHAR(255), mapPath VARCHAR(255), codeID int, tags VARCHAR(255), PRIMARY KEY(exampleID), FOREIGN KEY(codeID) REFERENCES codes(codeID))");
                } catch (SQLException e) {
                    try {
                        this.connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            if (!this.connect(false)) {
                System.out.println("Couldn't establish database connection");
            }
        }
        // printAllData();
    }

    private boolean connect(boolean create) {
        try {
            Class.forName(DBManager.DRIVER);
            this.connection = DriverManager.getConnection("jdbc:derby:" + DBManager.DB_NAME + ";create=" + create);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void shutdown() {
        try {
            if (this.connection != null) {
                try {
                    this.connection.close();
                } catch (SQLException e) {
                }
                DriverManager.getConnection("jdbc:derby:" + DB_NAME + ";shutdown=true");
            }
        } catch (SQLException ignored) {
        }
    }

    // Print all data of the tables
    private void printAllData() {
        Statement stmt = null;
        ResultSet res = null;
        try {
            if (this.connection == null && !this.connect(false)) {
                return;
            }
            stmt = this.connection.createStatement();
            String[][] tables = {{"examples", "5"}, {"codes", "2"}};
            for (String[] t : tables) {
                System.out.println("TABLE '" + t[0] + "' :");
                res = stmt.executeQuery("SELECT * FROM " + t[0]);
                while (res.next()) {
                    String output = "\t";
                    for (int i = 1; i <= Integer.parseInt(t[1]); i++) {
                        output += res.getString(i);
                        if (i != Integer.parseInt(t[1]))
                            output += ", ";
                    }

                    System.out.println(output);
                }
                System.out.println("\n");
            }

        } catch (SQLException e) {
            try {
                this.connection.close();
            } catch (SQLException ex) {
            }
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
