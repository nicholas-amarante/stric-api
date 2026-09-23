import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbCheckOwner {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5438/stric_db", "postgres", "postgres");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT v.titulo, u.email, u.nome FROM vagas v JOIN usuarios u ON v.recrutador_id = u.id WHERE v.id = '75062c5e-5683-4bbb-b7d3-1d4f10bc0514'");
            if (rs.next()) {
                System.out.println("Vaga: " + rs.getString("titulo") + " | Recrutador: " + rs.getString("nome") + " (" + rs.getString("email") + ")");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
