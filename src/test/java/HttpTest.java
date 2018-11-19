import love.moon.common.HttpResponse;
import love.moon.util.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;

public class HttpTest {

    public static void main(String[] args) throws IOException {
        String url = "http://localhost:8080/papa/test";
       url="https://cloud.keytop.cn/page/parking/lot_parking_query.html/?lotId=2870&lpn=苏E3K62V" +
               "&callbackUrl=https://cloud.keytop.cn/page/user/lpn/lpn_bind_v2.html?source=3&lotId=2870";
       url="https://cloud.keytop.cn/service/parking/queryWithLotId?sid=null&lpn=%E8%8B%8FE7T66V&lotId=2870&ocb=";
// HttpResponse response = HttpUtil.sendGet(url);
        HttpResponse response = sendGet(url);
        System.out.println("status code:" + response.getCode());
        System.out.println(response.getContent());

    }

    public static HttpResponse sendGet(String url) throws IOException {
        System.setProperty("http.proxyHost", "cn-proxy.jp.oracle.com");
        System.setProperty("http.proxyPort", "80");
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        String result = "";
        HttpResponse response = new HttpResponse();
        URL realURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) realURL.openConnection();
        ;
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Pragma", "no-cache");
        conn.setRequestProperty("no-cache", "0");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
        conn.connect();
        Map<String, List<String>> map = conn.getHeaderFields();
        for (String s : map.keySet()) {
            System.out.println(s + "-->" + map.get(s));
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        String line;
        while ((line = in.readLine()) != null) {
            result += "\n" + line;
        }
        response.setContent(result);
        response.setCode(conn.getResponseCode());
        response.setCookie(conn.getHeaderField("set-cookie"));
        return response;
    }
}
