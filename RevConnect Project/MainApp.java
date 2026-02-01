import java.util.List;
import java.util.Scanner;


public class MainApp {

    static Scanner sc = new Scanner(System.in);
    static UserDAO userDAO = new UserDAO();
    static ProfileDAO profileDAO = new ProfileDAO();
    static PostDAO postDAO = new PostDAO();
    static LikeDAO likeDAO = new LikeDAO();       // NEW
    static CommentDAO commentDAO = new CommentDAO();
    static NotificationDAO notificationDAO = new NotificationDAO();


    public static void main(String[] args) {

        while (true) {
            System.out.println("\n===== RevConnect =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> userDAO.registerUser();
                case 2 -> {
                    User user = userDAO.loginUser();
                    if (user != null) {
                        System.out.println("\nWelcome, " + user.getUsername());
                        dashboard(user);
                    }
                }
                case 3 -> System.exit(0);
                default -> System.out.println("Invalid choice");
            }
        }
    }

    // ---------------- DASHBOARD ----------------
    static void dashboard(User user) {

        while (true) {
            System.out.println("\n===== Dashboard =====");
            System.out.println("1. My Profile");
            System.out.println("2. Create Post");
            System.out.println("3. View My Posts");
            System.out.println("4. View Feed");
            System.out.println("5. Network");
            System.err.println("6. Notifications");
            System.out.println("7. Logout");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> myProfileMenu(user);
                case 2 -> createPost(user);
                case 3 -> viewMyPosts(user);
                case 4 -> viewFeed(user);
                case 5 -> networkMenu(user);
                case 6 -> notificationsMenu(user);
                case 7 -> {
                    System.out.println("Logged out");
                    return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    // ---------------- CREATE POST ----------------
    static void createPost(User user) {

        System.out.println("\n========================================");
        System.out.println("           Create a Post \n(Press Double Enter to Finish)");
        System.out.println("========================================");

        StringBuilder content = new StringBuilder();

        while (true) {
            String line = sc.nextLine();
            if (line.trim().isEmpty()) break;
            content.append(line).append("\n");
        }

        System.out.println("\n1. Post   2. Cancel");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            Post post = new Post();
            post.setUserId(user.getUserId());
            post.setContent(content.toString().trim());

            if (postDAO.createPost(post)) {
                System.out.println("Post published successfully");
            } else {
                System.out.println("Failed to publish post");
            }
        } else {
            System.out.println("Post cancelled");
        }
    }

    // ---------------- VIEW MY POSTS ----------------
static void viewMyPosts(User user) {

    List<Post> posts = postDAO.getPostsByUser(user.getUserId());

    System.out.println("\n========================================");
    System.out.println("           My Posts");
    System.out.println("========================================");

    if (posts.isEmpty()) {
        System.out.println("No posts yet");
        return;
    }

    int count = 1;
    for (Post p : posts) {
        String[] lines = p.getContent().split("\n");
        // Printing first line with number
        System.err.println(count + ". " + lines[0]);
        for (int i = 1; i < lines.length; i++) {
            System.out.println(" " + lines[i]);
        }
        count++;
    }

    // ---------------- EDIT / DELETE OPTIONS ----------------
    System.out.println("--------------------------------------------------");
    System.out.println("1. Edit Post   2. Delete Post   3. Back");
    System.out.print("Enter choice: ");
    int action = sc.nextInt();
    sc.nextLine();

    if (action == 3) return;

    System.out.print("Select post number: ");
    int postNo = sc.nextInt();
    sc.nextLine();

    if (postNo < 1 || postNo > posts.size()) {
        System.out.println("Invalid post selection");
        return;
    }

    Post selectedPost = posts.get(postNo - 1);

    switch (action) {
        case 1 -> {
            System.out.println("\nCurrent Content:\n" + selectedPost.getContent());
            System.out.println("\nEnter new content (Press Double Enter to finish):");

            StringBuilder newContent = new StringBuilder();
            while (true) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) break;
                newContent.append(line).append("\n");
            }

            if (postDAO.updatePost(selectedPost.getPostId(), newContent.toString().trim())) {
                System.out.println("Post updated successfully!");
            } else {
                System.out.println("Failed to update post.");
            }
        }
        case 2 -> {
            System.out.print("Are you sure you want to delete this post? (yes/no): ");
            String confirm = sc.nextLine().trim().toLowerCase();
            if (confirm.equals("yes")) {
                if (postDAO.deletePost(selectedPost.getPostId())) {
                    System.out.println("Post deleted successfully!");
                } else {
                    System.out.println("Failed to delete post.");
                }
            }
        }
        default -> System.out.println("Invalid option");
    }
}


    // ---------------- VIEW FEED ----------------
    static void viewFeed(User user) {

    List<Post> posts = postDAO.getFeed();

    System.out.println("\n========================================");
        System.out.println("           Feed");
        System.out.println("========================================");

    if (posts.isEmpty()) {
        System.out.println("No posts available");
        return;
    }

    int index = 1;
    for (Post p : posts) {

        String content = p.getContent()
                          .replace("\n", " ")
                          .trim();

        System.out.println(
            index + ". " + p.getUsername() + ": \"" + content + "\""
        );

        if (p.getHashtags() != null && !p.getHashtags().trim().isEmpty()) {
            System.out.println("   " + p.getHashtags());
        }

        System.out.println();
        index++;
    }

    System.out.println("--------------------------------------------------");
    System.out.println("1. Like   2. Comment   3. Repost   4. Back");
    System.out.print("Enter choice: ");

    int choice = sc.nextInt();
    sc.nextLine();

    if (choice == 4) return;

    System.out.print("Select post number: ");
    int postNo = sc.nextInt();
    sc.nextLine();

    if (postNo < 1 || postNo > posts.size()) {
        System.out.println("Invalid post selection");
        return;
    }

    Post selectedPost = posts.get(postNo - 1);

    switch (choice) {

        case 1 -> {
    if (likeDAO.likePost(user.getUserId(), selectedPost.getPostId())) {
        System.out.println("You liked post by " + selectedPost.getUsername());
    } else {
        System.out.println("Failed to like post (maybe already liked)");
    }
}

        case 2 -> {
            System.out.print("Enter your comment: ");
            String commentText = sc.nextLine();

            if (commentText.trim().isEmpty()) {
                System.out.println("Comment cannot be empty");
                return;
            }

            boolean added = commentDAO.addComment(
                selectedPost.getPostId(),
                user.getUserId(),
                commentText
            );

            if (added) {
                System.out.println("Comment added successfully");
            } else {
                System.out.println("Failed to add comment");
            }
        }

        case 3 -> {
            boolean reposted=postDAO.repost(
                selectedPost.getPostId(),
                user.getUserId() 
            );
            if (reposted) {
                System.out.println("Post reposted successfully");
            }
        }

        default -> System.out.println("Invalid option");
        }
    }

    // ---------------- MY PROFILE MENU ----------------
    static void myProfileMenu(User user) {

    while (true) {
        System.out.println("\n===== My Profile =====");
        System.out.println("1. Show Profile");
        System.out.println("2. Edit Profile");
        System.out.println("3. Delete Profile");
        System.out.println("4. Back");
        System.out.print("Choice: ");

        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1 -> showProfile(user);
            case 2 -> editProfile(user);
            case 3 -> {
                System.out.print("Are you sure you want to delete your profile? (yes/no): ");
                String confirm = sc.nextLine().trim().toLowerCase();
                if (confirm.equals("yes")) {
                    if (profileDAO.deleteProfile(user.getUserId())) {
                        System.out.println("Profile deleted successfully. Exiting...");
                        System.exit(0);
                    } else {
                        System.out.println("Failed to delete profile");
                    }
                }
            }
            case 4 -> { return; }
            default -> {
                    System.out.println("Invalid option");
                }
             }
        }
    }
    // ---------------- SHOW PROFILE ----------------
    static void showProfile(User user) {

    User fullProfile = profileDAO.getProfile(user.getUserId());
    if (fullProfile == null) {
        System.out.println("Profile not found.");
        return;
    }

    System.out.println("\n----- Profile Details -----");
    System.out.println("Username: " + fullProfile.getUsername());
    System.out.println("Email: " + fullProfile.getEmail());
    System.out.println("Full Name: " + nullToEmpty(fullProfile.getFullName()));
    System.out.println("Bio: " + nullToEmpty(fullProfile.getBio()));
    System.out.println("Location: " + nullToEmpty(fullProfile.getLocation()));
    System.out.println("Website: " + nullToEmpty(fullProfile.getWebsite()));
    System.out.println("Profile Pic Path: " + nullToEmpty(fullProfile.getProfilePic()));
    System.out.println("---------------------------");
}

static String nullToEmpty(String value) {
    return (value == null) ? "" : value;
}
    // ---------------- EDIT PROFILE ----------------
    static void editProfile(User user) {

    User fullProfile = profileDAO.getProfile(user.getUserId());
    if (fullProfile == null) {
        System.out.println("Profile not found. Creating new profile...");
        fullProfile = new User();
        fullProfile.setUserId(user.getUserId());
    }

    while (true) {
        System.out.println("\n--- Edit Profile ---");
        System.out.println("1. Full Name");
        System.out.println("2. Bio");
        System.out.println("3. Location");
        System.out.println("4. Website");
        System.out.println("5. Profile Pic Path");
        System.out.println("6. Back");
        System.out.print("Choose field to edit: ");

        int choice = sc.nextInt();
        sc.nextLine();

        String field = null;
        String value = null;

        switch (choice) {
            case 1 -> { field = "full_name"; System.out.print("Enter Full Name: "); value = sc.nextLine(); }
            case 2 -> { field = "bio"; System.out.print("Enter Bio: "); value = sc.nextLine(); }
            case 3 -> { field = "location"; System.out.print("Enter Location: "); value = sc.nextLine(); }
            case 4 -> { field = "website"; System.out.print("Enter Website: "); value = sc.nextLine(); }
            case 5 -> { field = "profile_pic"; System.out.print("Enter Profile Pic Path: "); value = sc.nextLine(); }
            case 6 -> { return; }
            default -> { System.out.println("Invalid option"); continue; }
        }

        if (field != null && value != null) {
            boolean updated = profileDAO.updateProfileField(user.getUserId(), field, value);
            if (updated) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Failed to update profile");
            }
        }
    }
}


// ---------------- NETWORK MENU ----------------
static NetworkDAO networkDAO = new NetworkDAO();

static void networkMenu(User user) {
    while (true) {
        System.out.println("\n===== Network =====");
        System.out.println("1. Find People");
        System.out.println("2. My Connections");
        System.out.println("3. Following");
        System.out.println("4. Back");
        System.out.println(); 
        System.out.print("Enter choice: ");
        int choice = sc.nextInt(); sc.nextLine();

        switch (choice) {
            case 1 -> findPeople(user);
            case 2 -> myConnections(user);
            case 3 -> followMenu(user);
            case 4 -> { return; }
            default -> System.out.println("Invalid choice");
        }
    }
}

static void findPeople(User user) {
    List<User> users = networkDAO.getAllOtherUsers(user.getUserId());
    if (users.isEmpty()) { 
        System.out.println("No users available"); 
        return; 
    }

    System.out.println(); 
    System.out.println("Available users to connect:");
    for (int i = 0; i < users.size(); i++)
        System.out.println((i+1) + ". " + users.get(i).getUsername());

    System.out.println(); 
    System.out.print("Select user to connect (number): ");
    int sel = sc.nextInt(); sc.nextLine();
    if (sel < 1 || sel > users.size()) { 
        System.out.println("Invalid selection"); 
        return; 
    }

    // Actually send the request in DB
    boolean success = networkDAO.sendConnectionRequest(user.getUserId(), users.get(sel-1).getUserId());
    if(success) {
        System.out.println("Connection request sent to " + users.get(sel-1).getUsername());
    } else {
        System.out.println("Failed to send request (maybe already sent)");
    }
}

static void incomingRequestsMenu(User user) {
    List<User> requests = networkDAO.getIncomingRequests(user.getUserId());
    if(requests.isEmpty()) { 
        System.out.println("No incoming requests"); 
        return; 
    }

    System.out.println("Incoming Requests:");
    for(int i=0; i<requests.size(); i++)
        System.out.println((i+1) + ". " + requests.get(i).getUsername());

    System.out.print("Select request number: ");
    int sel = sc.nextInt(); sc.nextLine();
    if(sel < 1 || sel > requests.size()) { 
        System.out.println("Invalid"); 
        return; 
    }

    System.out.println("1. Accept  2. Reject");
    int resp = sc.nextInt(); sc.nextLine();
    String status = (resp == 1) ? "ACCEPTED" : "REJECTED";

    boolean ok = networkDAO.respondToRequest(requests.get(sel-1).getUserId(), user.getUserId(), status);
    if(ok) System.out.println("Connection " + status.toLowerCase());
    else System.out.println("Failed to update request");
}

// My Connections + Incoming Requests
static void myConnections(User user) {

    // Show existing connections
    List<User> conns = networkDAO.getMyConnections(user.getUserId());
    System.out.println("\n===== My Connections =====");
    if (conns.isEmpty()) { 
        System.out.println("No connections yet"); 
    } else {
        for (int i = 0; i < conns.size(); i++)
            System.out.println((i+1) + ". " + conns.get(i).getUsername());
        System.out.println("\n1. Remove connection");
        System.out.println("2. Back");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt(); sc.nextLine();

if (choice == 1) {
    System.out.print("Enter connection number: ");
    int sel = sc.nextInt(); sc.nextLine();

    if (sel > 0 && sel <= conns.size()) {
        if (networkDAO.removeConnection(user.getUserId(), conns.get(sel-1).getUserId()))
            System.out.println("Connection removed successfully");
        else
            System.out.println("Failed to remove connection");
    } else {
        System.out.println("Invalid selection");
    }
}


    // Show incoming requests
List<User> requests = networkDAO.getIncomingRequests(user.getUserId());
System.out.println("\n===== Incoming Requests =====");

if (requests.isEmpty()) {
    System.out.println("No incoming requests");
    return;
}

// show first request only
User requester = requests.get(0);

System.out.println("\nRequest from: " + requester.getUsername());
System.out.println("1. Accept  2. Reject  3. Back");
System.out.print("Enter choice: ");

int reqchoice = sc.nextInt();
sc.nextLine();

if (choice == 1) {
    if (networkDAO.respondToRequest(
            requester.getUserId(),
            user.getUserId(),
            "ACCEPTED"))
        System.out.println("\nConnection accepted");
    else
        System.out.println("\nFailed to accept");

} else if (choice == 2) {
    if (networkDAO.respondToRequest(
            requester.getUserId(),
            user.getUserId(),
            "REJECTED"))
        System.out.println("\nConnection rejected");
    else
        System.out.println("\nFailed to reject");

} else {
    System.out.println("\nBack");
    }
}
}
static void followMenu(User user) {
    while (true) {
        System.out.println("\n===== Follow System =====");
        System.out.println("1. Follow Account");
        System.out.println("2. Unfollow Account");
        System.out.println("3. View Following");
        System.out.println("4. Back");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt(); sc.nextLine();

        switch (choice) {
            case 1 -> {
                List<User> users = networkDAO.getAllOtherUsers(user.getUserId());
                if (users.isEmpty()) { System.out.println("No accounts to follow"); continue; }

                System.out.println("Accounts available to follow:");
                for (int i = 0; i < users.size(); i++)
                    System.out.println((i+1) + ". " + users.get(i).getUsername());

                System.out.print("Select account number: ");
                int sel = sc.nextInt(); sc.nextLine();
                if (sel < 1 || sel > users.size()) { System.out.println("Invalid"); continue; }

                if (networkDAO.followAccount(user.getUserId(), users.get(sel-1).getUserId()))
                    System.out.println("You are now following " + users.get(sel-1).getUsername());
                else
                    System.out.println("Failed to follow");
            }
            case 2 -> {
                List<User> following = networkDAO.getFollowing(user.getUserId());
                if (following.isEmpty()) { System.out.println("No following accounts"); continue; }

                System.out.println("Select account to unfollow:");
                for (int i = 0; i < following.size(); i++)
                    System.out.println((i+1) + ". " + following.get(i).getUsername());

                int sel = sc.nextInt(); sc.nextLine();
                if (sel < 1 || sel > following.size()) { System.out.println("Invalid"); continue; }

                if (networkDAO.unfollowAccount(user.getUserId(), following.get(sel-1).getUserId()))
                    System.out.println("Unfollowed " + following.get(sel-1).getUsername());
            }
            case 3 -> {
                List<User> following = networkDAO.getFollowing(user.getUserId());
                System.out.println("Following:");
                for (int i = 0; i < following.size(); i++)
                    System.out.println((i+1) + ". " + following.get(i).getUsername());
            }
            case 4 -> { return; }
            default -> System.out.println("Invalid choice");
       
            }
        }
    }

    //notifications menu
    static void notificationsMenu(User user) {

    while (true) {
        System.out.println("\n[ Notifications ]");
        System.out.println("--------------------------------------------------");

        List<Notification> notifications =
                notificationDAO.getUnreadNotifications(user.getUserId());

        if (notifications.isEmpty()) {
            System.out.println("No notifications");
        } else {
            int i = 1;
            for (Notification n : notifications) {
                System.out.println(i + ". " + n.getMessage());
                i++;
            }
        }

        System.out.println("--------------------------------------------------");
        System.out.println("1. Mark All Read   2. View All   3. Back");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            notificationDAO.markAllRead(user.getUserId());
            System.out.println("All notifications marked as read");

        } else if (choice == 2) {
            showAllNotifications(user);

        } else {
            return;
        }
    }
}

// view all notification
static void showAllNotifications(User user) {

    System.out.println("\n[ All Notifications ]");
    System.out.println("--------------------------------------------------");

List<Notification> notifications =
        notificationDAO.getAllNotifications(user.getUserId());

if (notifications.isEmpty()) {
    System.out.println("No notifications");
} else {
    int i = 1;
    for (Notification n : notifications) {
        String status = n.isRead() ? "(Read)" : "(New)";
        System.out.println(i + ". " + n.getMessage() + " " + status);
        i++;
    }
}

    System.out.println("--------------------------------------------------");
    System.out.println("Press Enter to go back");
    sc.nextLine();
}
}