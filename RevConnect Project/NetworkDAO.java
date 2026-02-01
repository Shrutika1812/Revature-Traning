import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NetworkDAO {

    // Get all other users (to send connection requests)
    public List<User> getAllOtherUsers(int userId) {
        List<User> list = new ArrayList<>();
        String sql = """
            SELECT user_id, username
            FROM users
            WHERE user_id != ? 
            AND user_id NOT IN (
                SELECT receiver_id FROM connections WHERE sender_id = ? AND status='ACCEPTED'
            )
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //  Send a connection request
    public boolean sendConnectionRequest(int senderId, int receiverId) {
        String sql = "INSERT INTO connections(sender_id, receiver_id, status) VALUES(?, ?, 'PENDING')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Get incoming connection requests
    public List<User> getIncomingRequests(int userId) {
        List<User> list = new ArrayList<>();
        String sql = """
            SELECT u.user_id, u.username
            FROM connections c
            JOIN users u ON c.sender_id = u.user_id
            WHERE c.receiver_id = ? AND c.status='PENDING'
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Respond to connection request
    public boolean respondToRequest(int senderId, int receiverId, String status) {
        String sql = "UPDATE connections SET status=? WHERE sender_id=? AND receiver_id=? AND status='PENDING'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status); // "ACCEPTED" or "REJECTED"
            ps.setInt(2, senderId);
            ps.setInt(3, receiverId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get my accepted connections
    public List<User> getMyConnections(int userId) {
        List<User> list = new ArrayList<>();
        String sql = """
            SELECT u.user_id, u.username
            FROM connections c
            JOIN users u ON (c.sender_id = u.user_id AND c.receiver_id = ?) 
                          OR (c.receiver_id = u.user_id AND c.sender_id = ?)
            WHERE c.status='ACCEPTED'
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //  Remove a connection
    public boolean removeConnection(int userId, int connectionId) {
        String sql = "DELETE FROM connections WHERE (sender_id=? AND receiver_id=?) OR (sender_id=? AND receiver_id=?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, connectionId);
            ps.setInt(3, connectionId);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get accounts user is following
    public List<User> getFollowing(int userId) {
        List<User> list = new ArrayList<>();
        String sql = """
            SELECT u.user_id, u.username 
            FROM followers f
            JOIN users u ON f.following_user_id = u.user_id
            WHERE f.follower_user_id = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //  Follow an account
    public boolean followAccount(int followerId, int followingId) {
        String sql = "INSERT IGNORE INTO followers(follower_user_id, following_user_id) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Unfollow an account
    public boolean unfollowAccount(int followerId, int followingId) {
        String sql = "DELETE FROM followers WHERE follower_user_id=? AND following_user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
