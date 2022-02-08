package banking;

import java.sql.*;

public class SQL {
    public static void createNewTable(String filename) {
        String url = "jdbc:sqlite:" + filename;
        String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                + "    id INTEGER PRIMARY KEY, \n"
                + "    number TEXT, \n"
                + "    pin TEXT, \n"
                + "    balance INTEGER DEFAULT 0\n"
                + ");";
        try (Connection con = DriverManager.getConnection(url);
             Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
