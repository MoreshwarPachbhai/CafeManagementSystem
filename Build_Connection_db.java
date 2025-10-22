package CafeManagementSystemFolder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Build_Connection_db {
    public static Connection buildConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/CafeManagementSystem";
        String user = "postgres";
        String pass = "Pass@123";
        return DriverManager.getConnection(url, user, pass);
    }

    // Optional test
    public static void main(String[] args) throws SQLException, ClassNotFoundException{
        try (Connection c = buildConnection()) {
            if (c != null) System.out.println("Connected to PostgreSQL!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
