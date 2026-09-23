import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbCheckPatricia {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5438/stric_db", "postgres", "postgres");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT v.titulo FROM vagas v JOIN usuarios u ON v.recrutador_id = u.id WHERE u.email = 'patricia.diniz@headhunter.com.br'");
            while (rs.next()) {
                System.out.println("Vaga da Patricia: " + rs.getString("titulo"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
