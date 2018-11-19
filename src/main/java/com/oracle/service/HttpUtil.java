package com.oracle.service;

import love.moon.common.HttpResponse;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class HttpUtil {
    public static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

    public static HttpResponse sendGet(String url) throws IOException {
        CloseableHttpClient httpclient = createHttpClient(true);
        HttpGet httpGet = mockBrowserGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        StatusLine statusLine = response.getStatusLine();
        HttpResponse resp = new HttpResponse();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            resp.setCode(statusLine.getStatusCode());
        } else {
            System.out.println("ERROR,Status Code :" + statusLine.getStatusCode());
        }
        if (entity != null) {
            resp.setContent(EntityUtils.toString(entity));
        }
        return resp;
    }


    public static HttpResponse sendPost3(String url, Map<String, String> map) throws IOException {
        HttpResponse response = new HttpResponse();
        HttpURLConnection httpConn = null;
        try {
            URL obj = new URL(url);
            httpConn = (HttpURLConnection) obj.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Accept-Charset", "UTF-8");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0");
            for (String key : map.keySet()) {
                if (map.get(key) == null || map.get(key).equals("")) {
                    continue;
                }
                httpConn.setRequestProperty(key, map.get(key));
            }
//            httpConn.connect()
            InputStream in = httpConn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuffer temp = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null) {
                temp.append(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            response.setContent(temp.toString());
            LOG.info("Post end,responseCode:{}", httpConn.getResponseCode());
            response.setCode(httpConn.getResponseCode());
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
        return response;
    }


    public static int sendPost2(RequestBuilder builder) {
        CloseableHttpClient client = null;
        try {

            client = HttpClients.custom().build();

            HttpUriRequest request = builder.build();
            org.apache.http.HttpResponse response = client.execute(request);
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

    public static int sendPost(String url, List<NameValuePair> formParams) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            httppost.setEntity(uefEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                }
                return response.getStatusLine().getStatusCode();
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    private static HttpGet mockBrowserGet(String url) {
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

    private static CloseableHttpClient createHttpClient(boolean isProxy) {
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
