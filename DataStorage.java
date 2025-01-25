import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class DataStorage {
    private Map<String, User> users;
    private Map<Integer, Link> globalLinks;
    private CreateLink createLink;

    public DataStorage() {
        this.users = new HashMap<>();
        this.globalLinks = new HashMap<>();
        this.createLink = new CreateLink();
        preloadData();
    }

    private void preloadData() {
        addUser("aaa", "1234");
        addUser("bbb", "4321");
        addGlobalLink(1, "https://github.com");
        addGlobalLink(2, "https://stackoverflow.com");
    }

    public boolean addUser(String username, String password) {
        if (users.containsKey(username)) {
            return false; // User already exists
        }
        users.put(username, new User(username, password));
        return true;
    }

    public boolean removeUser(String username, String password) {
        User user = validateUser(username, password);
        if (user != null) {
            users.remove(username);
            return true;
        }
        return false;
    }

    public boolean displayUserLinks(String username, String password) {
        User user = validateUser(username, password);
        if (user == null) {
            return false;
        }

        System.out.println("Displaying links for user: " + username);
        for (Link link : user.getLinks().values()) {
            System.out.println(link.getOriginalUrl());
        }
        return true;
    }

    public boolean saveAllUserLinks(String username, String password) {
        User user = validateUser(username, password);
        if (user == null) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(username + "_links.txt"))) {
            for (Link link : user.getLinks().values()) {
                writer.write(link.getOriginalUrl() + "\n");
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String createShortLink(String username, String password, int linkId, long duration, int maxUses) {
        User user = validateUser(username, password);
        if (user == null || !globalLinks.containsKey(linkId)) {
            return null;
        }

        Link originalLink = globalLinks.get(linkId);
        String shortUrl = createLink.processLink(originalLink.getOriginalUrl());
        Link shortLink = new Link(shortUrl, duration, maxUses);
        user.getLinks().put(linkId, shortLink);
        return shortUrl;
    }

    public String useFastLink(String username, String password, int linkId) {
        User user = validateUser(username, password);
        if (user == null) {
            return "Invalid username or password.";
        }

        if (!user.getLinks().containsKey(linkId)) {
            return "No fast link available for this user.";
        }

        Link link = user.getLinks().get(linkId);
        if (!link.isUsable()) {
            Link newLink = new Link(link.getOriginalUrl(), 3600, 10); // Новый срок действия 1 час, 10 использований
            user.getLinks().put(linkId, newLink);
            return "Link was too old. Made new one. FAST LINK WAS USED SUCCESS!";
        }

        link.incrementUsage();
        return "FAST LINK WAS USED SUCCESS!";
    }

    public void addGlobalLink(int id, String originalUrl) {
        if (!globalLinks.containsKey(id)) {
            globalLinks.put(id, new Link(originalUrl));
        }
    }

    public void displayGlobalLinks() {
        System.out.println("Global Links:");
        for (Map.Entry<Integer, Link> entry : globalLinks.entrySet()) {
            System.out.println("ID: " + entry.getKey() + " URL: " + entry.getValue().getOriginalUrl());
        }
    }

    private User validateUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public static class User {
        private String username;
        private String password;
        private Map<Integer, Link> links;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
            this.links = new HashMap<>();
        }

        public String getPassword() {
            return password;
        }

        public Map<Integer, Link> getLinks() {
            return links;
        }

        public void addLink(int id, String originalUrl) {
            links.put(id, new Link(originalUrl));
        }
    }

    public static class Link {
        private String originalUrl;
        private long expirationTime;
        private int maxUses;
        private int currentUses;

        public Link(String originalUrl) {
            this.originalUrl = originalUrl;
            this.expirationTime = Long.MAX_VALUE;
            this.maxUses = Integer.MAX_VALUE;
            this.currentUses = 0;
        }

        public Link(String originalUrl, long duration, int maxUses) {
            this.originalUrl = originalUrl;
            this.expirationTime = System.currentTimeMillis() + duration * 1000;
            this.maxUses = maxUses;
            this.currentUses = 0;
        }

        public String getOriginalUrl() {
            return originalUrl;
        }

        public boolean isUsable() {
            return currentUses < maxUses && System.currentTimeMillis() < expirationTime;
        }

        public void incrementUsage() {
            currentUses++;
        }
    }
}

