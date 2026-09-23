import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbLogin2 {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5438/stric_db", "postgres", "postgres");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT email, nome FROM usuarios WHERE role = 'ROLE_RECRUITER'");
            while (rs.next()) {
                System.out.println("Email: " + rs.getString("email") + ", Nome: " + rs.getString("nome"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
