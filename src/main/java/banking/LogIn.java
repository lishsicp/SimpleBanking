package banking;

import java.sql.*;

public class LogIn {
    private String number;
    private String pin;
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
    public void getLogInInfo(String card) {
        String sql = "SELECT number, pin FROM card WHERE number = ?";
        try (Connection conn = this.connect();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, card);
            ResultSet rs    = ps.executeQuery();
            while (rs.next()) {
                this.number = rs.getString("number");
                this.pin = rs.getString("pin");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean getState(String a, String b) {
        return number != null && (this.number.equals(a) && this.pin.equals(b));
    }

    public String setNumber(String number) {
        return this.number = number;
    }
    public String setPin(String pin) {
        return this.pin = pin;
    }
    public String getNumber() {
        return this.number;
    }
    public String getPin() {
        return this.pin;
    }

}
