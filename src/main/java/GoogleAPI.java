import java.awt.Image;
import java.io.IOException;
import java.util.List;

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
        try {
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());
            final JSONArray items = responseBody.getJSONArray("items");

            for (int i=0; i<items.length(); i++) {
                String link = items.getJSONObject(i).getString("link");
                System.out.println(link);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
