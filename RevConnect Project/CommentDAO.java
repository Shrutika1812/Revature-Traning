import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommentDAO {

    public boolean addComment(int postId, int userId, String commentText) {

        String sql = "INSERT INTO comments (post_id, user_id, comment_text) VALUES (?, ?, ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ps.setString(3, commentText);

            boolean added = ps.executeUpdate() > 0;

            // 👉 ADD THIS LINE
            if (added) {
                addCommentNotification(con, postId, userId);
            }

            return added;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //  already correct
    private void addCommentNotification(Connection con, int postId, int senderId)
            throws SQLException {

        String sql = """
            INSERT INTO notifications (user_id, sender_username, message, notification_type)
            SELECT 
                p.user_id,
                u.username,
                CONCAT(u.username, ' commented on your post'),
                'COMMENT'
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
