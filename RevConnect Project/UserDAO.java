import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class UserDAO {

    Scanner sc = new Scanner(System.in);

    // ---------------- REGISTER ----------------
    public void registerUser() {

        System.out.println("\n========================================");
        System.out.println("             Registration");
        System.out.println("========================================");

        System.out.println("\nSelect User Type:");
        System.out.println("1. Personal");
        System.out.println("2. Content Creator");
        System.out.println("3. Business");
        System.out.print("Choice: ");

        int typeChoice = sc.nextInt();
        sc.nextLine();

        String userType;
        switch (typeChoice) {
            case 1 -> userType = "PERSONAL";
            case 2 -> userType = "CREATOR";
            case 3 -> userType = "BUSINESS";
            default -> {
                System.out.println("Invalid user type");
                return;
            }
        }

        System.out.print("\nUsername: ");
        String username = sc.nextLine().trim();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        // -------- VALIDATIONS --------
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required to Register");
            return;
        }

        // USERNAME MUST CONTAIN AT LEAST ONE LETTER
        if (!username.matches(".*[a-zA-Z].*")) {
            System.out.println("Username must contain at least one alphabet");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("Invalid email format");
            return;
        }

        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters");
            return;
        }

        String sql = """
            INSERT INTO users (username, email, password, user_type)
            VALUES (?, ?, ?, ?)
        """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, userType);

            ps.executeUpdate();
            System.out.println("\nRegistration successful!!!");

        } catch (Exception e) {
            System.out.println("Registration failed (Email may already exist)");
        }
    }

    // ---------------- LOGIN ----------------
public User loginUser() {

    System.out.println("\n================================");
    System.out.println("              Login");
    System.out.println("================================");

    System.out.print("Email: ");
    String email = sc.nextLine().trim();

    System.out.print("Password: ");
    String password = sc.nextLine().trim();

    if (email.isEmpty() || password.isEmpty()) {
        System.out.println("Email and Password cannot be empty");
        return null;
    }

    try (Connection con = DBConnection.getConnection()) {

        //  STEP 1: CHECK IF USER EXISTS
        String checkUserSql = "SELECT * FROM users WHERE email = ?";
        PreparedStatement ps1 = con.prepareStatement(checkUserSql);
        ps1.setString(1, email);
        ResultSet rs1 = ps1.executeQuery();

        if (!rs1.next()) {
            System.out.println("User not found. Please register first.");
            return null;
        }

        //  STEP 2: CHECK PASSWORD
        String loginSql = "SELECT * FROM users WHERE email = ? AND password = ?";
        PreparedStatement ps2 = con.prepareStatement(loginSql);
        ps2.setString(1, email);
        ps2.setString(2, password);
        ResultSet rs2 = ps2.executeQuery();

        if (rs2.next()) {
            System.out.println("\nLogin successful!!!");

            User user = new User();
            user.setUserId(rs2.getInt("user_id"));
            user.setUsername(rs2.getString("username"));
            user.setEmail(rs2.getString("email"));
            return user;
        } else {
            System.out.println("Incorrect password");
            return null;
        }

    } catch (Exception e) {
        System.out.println("Login error");
        return null;
    }
   }   
}
