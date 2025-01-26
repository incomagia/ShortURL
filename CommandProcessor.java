import java.util.Scanner;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

public class CommandProcessor {
    private DataStorage dataStorage;

    public CommandProcessor(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public void processCommand(String commandLine) {
        String[] parts = commandLine.split(" ");
        String command = parts[0].toLowerCase();

        switch (command) {
            case "addu":
                if (parts.length < 3) {
                    System.out.println("Usage: addu <username> <password>");
                } else {
                    String username = parts[1];
                    String password = parts[2];
                    boolean success = dataStorage.addUser(username, password);
                    if (success) {
                        System.out.println("User added successfully.");
                    } else {
                        System.out.println("User already exists.");
                    }
                }
                break;

            case "remu":
                if (parts.length < 3) {
                    System.out.println("Usage: remu <username> <password>");
                } else {
                    String username = parts[1];
                    String password = parts[2];
                    boolean success = dataStorage.removeUser(username, password);
                    if (success) {
                        System.out.println("User removed successfully.");
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                }
                break;

            case "seel":
                if (parts.length < 3) {
                    System.out.println("Usage: seel <username> <password>");
                } else {
                    String username = parts[1];
                    String password = parts[2];
                    boolean success = dataStorage.displayUserLinks(username, password);
                    if (!success) {
                        System.out.println("Invalid username or password.");
                    }
                }
                break;

            case "dol":
                if (parts.length < 6) {
                    System.out.println("Usage: dol <username> <password> <linkId> <duration> <maxUses>");
                } else {
                    String username = parts[1];
                    String password = parts[2];
                    try {
                        int linkId = Integer.parseInt(parts[3]);
                        long duration = Long.parseLong(parts[4]);
                        int maxUses = Integer.parseInt(parts[5]);
                        String shortLink = dataStorage.createShortLink(username, password, linkId, duration, maxUses);
                        if (shortLink != null) {
                            System.out.println("Short link created: " + shortLink);
                        } else {
                            System.out.println("Failed to create short link. Invalid credentials or link ID.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Duration and maxUses must be numbers.");
                    }
                }
                break;

            case "use":
                if (parts.length < 4) {
                    System.out.println("Usage: use <username> <password> <linkId>");
                } else {
                    String username = parts[1];
                    String password = parts[2];
                    try {
                        int linkId = Integer.parseInt(parts[3]);
                        String result = dataStorage.useFastLink(username, password, linkId);
                        System.out.println(result);

                        if (result.startsWith("http")) {
                            try {
                                Desktop.getDesktop().browse(new URI(result));
                            } catch (IOException | URISyntaxException e) {
                                System.out.println("Failed to open link in browser: " + e.getMessage());
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Link ID must be a number.");
                    }
                }
                break;

            case "del":
                if (parts.length < 4) {
                    System.out.println("Usage: del <username> <password> <linkId>");
                } else {
                    String username = parts[1];
                    String password = parts[2];
                    try {
                        int linkId = Integer.parseInt(parts[3]);
                        boolean success = dataStorage.deleteUserLink(username, password, linkId);
                        if (success) {
                            System.out.println("Link removed successfully.");
                        } else {
                            System.out.println("Failed to remove link. Invalid credentials or link ID.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Link ID must be a number.");
                    }
                }
                break;

            case "lchen":
                if (parts.length < 6) {
                    System.out.println("Usage: lchen <username> <password> <linkId> <newDuration> <newMaxUses>");
                } else {
                    String username = parts[1];
                    String password = parts[2];
                    try {
                        int linkId = Integer.parseInt(parts[3]);
                        long newDuration = Long.parseLong(parts[4]);
                        int newMaxUses = Integer.parseInt(parts[5]);
                        boolean success = dataStorage.editLinkParameters(username, password, linkId, newDuration, newMaxUses);
                        if (success) {
                            System.out.println("Link parameters updated successfully.");
                        } else {
                            System.out.println("Failed to update link. Invalid credentials or link ID.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Duration and maxUses must be numbers.");
                    }
                }
                break;

            case "save":
                if (parts.length < 3) {
                    System.out.println("Usage: save <username> <password>");
                } else {
                    String username = parts[1];
                    String password = parts[2];
                    boolean success = dataStorage.saveAllUserLinks(username, password);
                    if (success) {
                        System.out.println("Links saved successfully.");
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                }
                break;

            case "help":
                System.out.println("Available commands:");
                System.out.println("addu <username> <password> - Add a new user.");
                System.out.println("remu <username> <password> - Remove an existing user.");
                System.out.println("seel <username> <password> - View all links of a user.");
                System.out.println("dol <username> <password> <linkId> <duration> <maxUses> - Create a short link for a user.");
                System.out.println("use <username> <password> <linkId> - Use the ID available fast link.");
                System.out.println("del <username> <password> <linkId> - Remove a link for a user.");
                System.out.println("lchen <username> <password> <linkId> <newDuration> <newMaxUses> - Edit link parameters.");
                System.out.println("save <username> <password> - Save all user links to a file.");
                System.out.println("help - Show this help message.");
                break;

            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
                break;
        }
    }
}
