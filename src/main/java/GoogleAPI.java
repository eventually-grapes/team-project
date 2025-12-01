import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleAPI {
    private final OkHttpClient client = new OkHttpClient();
    Dotenv dotenv = Dotenv.load();

    public List<Image> getImages(String query) throws IOException {
        String KEY = dotenv.get("KEY");
        String CX = dotenv.get("CX");
        if (KEY == null || KEY.isBlank() || CX == null || CX.isBlank()) {
            throw new IllegalStateException("Missing Google API credentials, set environment variables KEY and CX in .env");
        }

        final String encoded_query = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://www.googleapis.com/customsearch/v1?cx=" + CX + "&key=" + KEY + "&q=" + encoded_query + "&num=3&searchType=image";
        
        Request request = new Request.Builder()
                .url(url)
                .build();
        ArrayList<Image> images = new ArrayList<>();
        try {
            final Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                String bodyText = response.body() != null ? response.body().string() : "";
                throw new IOException("Google API request failed: HTTP " + response.code() + " - " + response.message() + (bodyText.isBlank() ? "" : " - " + bodyText));
            }
            
            String responseBody = response.body() != null ? response.body().string() : "";
            if (responseBody.isBlank()) {
                return Collections.emptyList();
            }

            JSONObject root;
            try {
                root = new JSONObject(responseBody);
            } catch (Exception je) {
                throw new IOException("Failed to parse Google API response as JSON: " + je.getMessage(), je);
            }

            JSONArray items = root.optJSONArray("items");
            if (items == null || items.length() == 0) {
                return Collections.emptyList();
            }

            for (int i=0; i<items.length(); i++) {
                String imageURL = items.getJSONObject(i).getString("link");

                Request imageRequest = new Request.Builder()
                    .url(imageURL)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0 Safari/537.36")
                    .get()
                    .build();
                
                try (Response imageResponse = client.newCall(imageRequest).execute()) {
                    if (!imageResponse.isSuccessful() || imageResponse.body() == null) {
                        System.err.println("Skipping image (HTTP " + (imageResponse != null ? imageResponse.code() : -1) + "): " + imageURL);
                        continue;
                    }

                    try (InputStream in = imageResponse.body().byteStream()) {
                        BufferedImage image = ImageIO.read(in);
                        if (image != null) {
                            images.add(image);
                        } else {
                            System.err.println("ImageIO couldn't decode image at: " + imageURL);
                        }
                    } catch (IOException ii) {
                        System.err.println("Failed to read image bytes from: " + imageURL + " -> " + ii.getMessage());
                    }
                } catch (Exception exImg) {
                    System.err.println("Failed to fetch image URL: " + imageURL + " -> " + exImg.getMessage());
                }
                System.out.println(imageURL);
            }
            return images;
            
        } catch (IOException e) {
            throw new IOException("Failed to call Google API: " + e.getMessage(), e);
        }
    }
}
