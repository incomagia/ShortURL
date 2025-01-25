import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class CreateLink {

    public String generateShortLink(String originalUrl) {
        try {
            String uniqueUrl = originalUrl + "?id=" + UUID.randomUUID();
            String apiUrl = "https://clck.ru/--?url=" + uniqueUrl;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return "Failed to generate short link. HTTP Status: " + response.statusCode();
            }
        } catch (Exception e) {
            return "Error occurred: " + e.getMessage();
        }
    }

    public String processLink(String originalUrl) {
        return generateShortLink(originalUrl);
    }
}

