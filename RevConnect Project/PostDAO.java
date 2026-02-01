import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    // CREATE POST
    public boolean createPost(Post post) {

        String sql = "INSERT INTO posts (user_id, content, hashtags) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, post.getUserId());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getHashtags());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // VIEW MY POSTS
    public List<Post> getPostsByUser(int userId) {

    List<Post> list = new ArrayList<>();

    String sql = """
        SELECT post_id, content, hashtags
        FROM posts
        WHERE user_id = ?
        ORDER BY created_at DESC
    """;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Post p = new Post();
            p.setPostId(rs.getInt("post_id")); // ✅ MOST IMPORTANT LINE
            p.setContent(rs.getString("content"));
            p.setHashtags(rs.getString("hashtags"));
            list.add(p);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}


    // VIEW FEED
    public List<Post> getFeed() {

        List<Post> list = new ArrayList<>();

        String sql = """
            SELECT p.post_id, p.content, p.hashtags, u.username
            FROM posts p
            JOIN users u ON p.user_id = u.user_id
            ORDER BY p.created_at DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Post p = new Post();
                p.setPostId(rs.getInt("post_id"));
                p.setUsername(rs.getString("username"));
                p.setContent(rs.getString("content"));
                p.setHashtags(rs.getString("hashtags"));
                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // LIKE POST
    public boolean likePost(int postId, int userId) {

        String sql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Already liked or error occurred");
        }
        return false;
    }

    //  REPOST (INSIDE CLASS — THIS WAS THE BUG)
    public boolean repost(int originalPostId, int userId) {

        String sql = """
            INSERT INTO posts (user_id, content, hashtags)
            SELECT ?, content, hashtags
            FROM posts
            WHERE post_id = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, originalPostId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

// UPDATE POST CONTENT
public boolean updatePost(int postId, String content) {

    String sql = "UPDATE posts SET content = ? WHERE post_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, content);
        ps.setInt(2, postId);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

// DELETE POST
public boolean deletePost(int postId) {

    String sql = "DELETE FROM posts WHERE post_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, postId);
        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
}