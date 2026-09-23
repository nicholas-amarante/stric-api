import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbCheck {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5438/stric_db", "postgres", "postgres");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM candidaturas");
            rs.next();
            System.out.println("Candidaturas count: " + rs.getInt(1));
            
            rs = stmt.executeQuery("SELECT id, status, vaga_id FROM candidaturas LIMIT 5");
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("id") + ", STATUS: " + rs.getString("status") + ", VAGA: " + rs.getString("vaga_id"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
