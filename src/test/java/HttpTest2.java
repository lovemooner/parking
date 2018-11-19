import love.moon.common.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpTest2 {

    public static void main(String[] args) throws IOException {
       String url="https://cloud.keytop.cn/page/parking/lot_parking_query.html/?lotId=2870&lpn=苏E3K62V" +
                "&callbackUrl=https://cloud.keytop.cn/page/user/lpn/lpn_bind_v2.html?source=3&lotId=2870";
       url="https://cloud.keytop.cn/page/parking/lot_parking_query.html?lotId=2870&lpn=苏E3K62V&callbackUrl=https%3A%2F%2Fcloud.keytop.cn%2Fpage%2Fuser%2Flpn%2Flpn_bind_v2.html%3Fsource%3D3%26lotId%3D2870&source=3";
       url="https://cloud.keytop.cn/service/parking/queryWithLotId?sid=null&lpn=%E8%8B%8FE7T66V&lotId=2870&ocb=";
       HttpTest2 t=new HttpTest2();
       HttpResponse response = t.sendGet(url);
       System.out.println("status code:" + response.getCode());
       System.out.println(response.getContent());

    }



    public  HttpResponse sendGet(String url) throws IOException {
        CloseableHttpClient httpclient = createHttpClient(true);
        HttpGet httpGet = mockBrowserGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        StatusLine statusLine = response.getStatusLine();
        HttpResponse resp=new HttpResponse();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
        } else {
            System.out.println("ERROR,Status Code :" + statusLine.getStatusCode());
        }
        if (entity != null) {
//                    System.out.println("Response content length: " + entity.getContentLength());
            String responseStr = "Response content: " + EntityUtils.toString(entity);
                    System.out.println(responseStr); // 打印响应内容
            resp.setContent(responseStr);
        }
        return resp;
    }

    protected HttpGet mockBrowserGet(String url) {
        HttpGet httpGet = new HttpGet(url);
//        httpGet.setHeader("Accept", "Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        httpGet.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
//        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
//        httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
//        httpGet.setHeader("Connection", "keep-alive");
//        httpGet.setHeader("Cookie", "");
//        httpGet.setHeader("Host", "");
//        httpGet.setHeader("refer", "");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
        //            getMethod.getParams()
//                    .setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        return httpGet;
    }

    private CloseableHttpClient createHttpClient(boolean isProxy) {
        CloseableHttpClient httpclient;
        if (isProxy) {
            HttpHost proxy = new HttpHost("cn-proxy.jp.oracle.com", 80, "http");
            RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
            //实例化CloseableHttpClient对象
            httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        } else {
            httpclient = HttpClients.createDefault();
        }
        return httpclient;
    }
}
