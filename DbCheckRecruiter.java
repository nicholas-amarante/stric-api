import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbCheckRecruiter {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5438/stric_db", "postgres", "postgres");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT email, password FROM usuarios WHERE role = 'ROLE_RECRUITER' LIMIT 1");
            if (rs.next()) {
                System.out.println("Email: " + rs.getString("email"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
