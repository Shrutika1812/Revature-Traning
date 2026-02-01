import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LikeDAO {

    // Like a post
    public boolean likePost(int userId, int postId) {
        String sql = "INSERT IGNORE INTO likes(user_id, post_id) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, postId);

            boolean liked = ps.executeUpdate() > 0;

            // 👉 ADD THIS
            if (liked) {
                addLikeNotification(con, postId, userId);
            }

            return liked;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //  ADD THIS METHOD (NEW)
    private void addLikeNotification(Connection con, int postId, int senderId)
            throws SQLException {

        String sql = """
            INSERT INTO notifications (user_id, sender_username, message, notification_type)
            SELECT 
                p.user_id,
                u.username,
                CONCAT(u.username, ' liked your post'),
                'LIKE'
            FROM posts p
            JOIN users u ON u.user_id = ?
            WHERE p.post_id = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, postId);
            ps.executeUpdate();
        }
    }
}
