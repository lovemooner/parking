import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HttpTest3 {
    private static String URL_POST_PARKING_INFO = "https://apex.oracle.com/pls/apex/fbi/parking/parkingInfo";

    public static void main(String[] args) {
        RequestBuilder builder = RequestBuilder.get()
                .setUri(URL_POST_PARKING_INFO);
//        builder.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        builder.setHeader("comeTime", "2018-11-14 08:33:01");
        builder.setHeader("lotName", "201801");
        int statusCode = sendPost(builder);
    }

    public static int sendPost(RequestBuilder builder) {
        CloseableHttpClient client = null;
        try {
            client = HttpClients.custom().build();
            HttpUriRequest request = builder.build();
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
            }
            return response.getStatusLine().getStatusCode();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
