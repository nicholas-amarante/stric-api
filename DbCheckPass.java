import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbCheckPass {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5438/stric_db", "postgres", "postgres");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT email, senha FROM usuarios WHERE email = 'ana.souza@techrecruiting.com.br'");
            if (rs.next()) {
                System.out.println("Email: " + rs.getString("email") + " | Hash: " + rs.getString("senha"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
