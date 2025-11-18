import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONObject;
import org.json.JSONArray;

public class GoogleAPI {
    private final OkHttpClient client = new OkHttpClient();

    public List<Image> getImages(String query) {
        String url = "https://www.googleapis.com/customsearch/v1?cx=" + System.getenv("CX") + "&key=" + System.getenv("KEY") + "&q=" + query + "&num=3&searchType=image";
        Request request = new Request.Builder()
                .url(url)
                .build();
        ArrayList<Image> images = new ArrayList<>();
        try {
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());
            final JSONArray items = responseBody.getJSONArray("items");

            for (int i=0; i<items.length(); i++) {
                URL imageURL = new URL(items.getJSONObject(i).getString("link"));
                Image image = ImageIO.read(imageURL);
                images.add(image);
                System.out.println(imageURL);
            }
            return images;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
