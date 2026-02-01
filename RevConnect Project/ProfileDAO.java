import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileDAO {

    // ---------------- GET PROFILE ----------------
    public User getProfile(int userId) {
        String sql = """
            SELECT u.username, u.email, u.user_type,
                   p.full_name, p.bio, p.location, p.website, p.profile_pic
            FROM users u
            LEFT JOIN profiles p ON u.user_id = p.user_id
            WHERE u.user_id = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(userId);
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                // profile fields
                user.setFullName(rs.getString("full_name"));
                user.setBio(rs.getString("bio"));
                user.setLocation(rs.getString("location"));
                user.setWebsite(rs.getString("website"));
                user.setProfilePic(rs.getString("profile_pic"));

                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ---------------- UPDATE PROFILE FIELD ----------------
    public boolean updateProfileField(int userId, String fieldName, String value) {
        // check if profile exists
        if (!profileExists(userId)) {
            // insert new row in profiles
            String insertSql = "INSERT INTO profiles (user_id, " + fieldName + ") VALUES (?, ?)";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(insertSql)) {

                ps.setInt(1, userId);
                ps.setString(2, value);
                return ps.executeUpdate() > 0;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // update existing profile
            String sql = "UPDATE profiles SET " + fieldName + "=? WHERE user_id=?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, value);
                ps.setInt(2, userId);
                return ps.executeUpdate() > 0;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    // ---------------- DELETE PROFILE ----------------
    public boolean deleteProfile(int userId) {
        // delete from profiles table
        String sqlProfiles = "DELETE FROM profiles WHERE user_id=?";
        String sqlUsers = "DELETE FROM users WHERE user_id=?"; // will cascade posts/comments/likes

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps1 = con.prepareStatement(sqlProfiles);
             PreparedStatement ps2 = con.prepareStatement(sqlUsers)) {

            ps1.setInt(1, userId);
            ps1.executeUpdate();

            ps2.setInt(1, userId);
            ps2.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------------- CHECK IF PROFILE EXISTS ----------------
    private boolean profileExists(int userId) {
        String sql = "SELECT profile_id FROM profiles WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
