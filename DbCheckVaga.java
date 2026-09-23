import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbCheckVaga {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5438/stric_db", "postgres", "postgres");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, titulo FROM vagas WHERE titulo LIKE '%Java%'");
            while (rs.next()) {
                String id = rs.getString("id");
                String titulo = rs.getString("titulo");
                System.out.println("VAGA ID: " + id + ", TITULO: " + titulo);
                
                Statement stmt2 = conn.createStatement();
                ResultSet rs2 = stmt2.executeQuery("SELECT count(*) FROM candidaturas WHERE vaga_id = '" + id + "'");
                rs2.next();
                System.out.println("  -> Candidaturas count: " + rs2.getInt(1));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
