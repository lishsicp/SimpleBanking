package banking;

import java.sql.*;

public class SQLQueries {

    private Connection connect() {
        String url = "jdbc:sqlite:" + Main.filename;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public void selectAll(){
        String sql = "SELECT id, number, pin, balance FROM card";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("number") + "\t" +
                        rs.getString("pin") + "\t" +
                        rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public int getBalance(String card) {
        String sql = "SELECT balance FROM card WHERE number = ?";
        int balance = 0;
        try (Connection conn = this.connect();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, card);
            ResultSet rs    = ps.executeQuery();
            while (rs.next()) {
                balance = rs.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return balance;
    }
    public void setBalance(String number, int balance) {
        String sql = "UPDATE card SET balance = balance + ? "
                + "WHERE number = ?";
        try (Connection conn = this.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(2, number);
            ps.setInt(1, balance);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void transfer(String number, int balance) {
        String sql = "UPDATE card SET balance = balance - ? "
                + "WHERE number = ?";
        try (Connection conn = this.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(2, number);
            ps.setInt(1, balance);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void deleteCard(String card){
        String sql = "DELETE FROM card WHERE number = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            // execute the delete statement
            pstmt.setString(1, card);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean cardExists(String card) {
        String sql = "SELECT number FROM card WHERE number = ?";
        String res = "";
        try (Connection conn = this.connect();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, card);
            ResultSet rs    = ps.executeQuery();
            while (rs.next()) {
                res = "" + rs.getString("number");
                // System.out.println(rs.getBoolean("number"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res.equals(card);
    }
}
