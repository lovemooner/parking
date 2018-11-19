package com.oracle.service;

import com.oracle.model.Car;
import com.oracle.model.ParkingInfo;
import com.oracle.pojo.*;
import love.moon.common.HttpResponse;
import love.moon.util.DateUtil;
import love.moon.util.JsonUtil;
import love.moon.util.NumberUtil;
import love.moon.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingService {
    public static final Logger LOG = LoggerFactory.getLogger(ParkingService.class);





    public void startCacheCarThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpResponse response = HttpUtil.sendGet(Constants.URL_GET_CAR_COUNT);
                    Counter counter = JsonUtil.jsonToObj(response.getContent(), Counter.class);
                    if (counter.getCount() != 0 && counter.getCount() != CacheAdapter.getCarList().size()) {
                        LOG.info("startCacheCarThread-->update Car list");
                        CacheAdapter.setCarList(requestCarList());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1 * 60 * 1000l);
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }).start();
    }

    public void reBuildParkingMap() {
        List<GetCurrentParkingResult.CurrentParkingInfo> parkingInfoList = requestCurrentParking();
        if (CollectionUtils.isNotEmpty(parkingInfoList)) {
            for (GetCurrentParkingResult.CurrentParkingInfo parkingInfo : parkingInfoList) {
                CacheAdapter.getParkingMap().put(parkingInfo.getCarId(), parkingInfo.getId());
            }
        }
    }

    public List<GetCurrentParkingResult.CurrentParkingInfo> requestCurrentParking() {
        try {
            HttpResponse response = HttpUtil.sendGet(Constants.URL_GET_CURRENT_PARKING);
            GetCurrentParkingResult result = JsonUtil.jsonToObj(response.getContent(), GetCurrentParkingResult.class);
            return result.getItems();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public List<Car> requestCarList() {
        LOG.info("sendGet car List ");
        try {
            HttpResponse response = HttpUtil.sendGet(Constants.URL_GET_CAR);
            GetCarResult result = JsonUtil.jsonToObj(response.getContent(), GetCarResult.class);
            LOG.info(JsonUtil.jsonStandardizing(JsonUtil.objectToJson(result)));
            return result.getItems();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }

    }

    public HttpResponse  postParkingInfo(ParkingInfo parkingInfo) {
        Map<String, String> map = new HashMap<>();
        if(parkingInfo.getId()!=null){
            map.put("id",String.valueOf(parkingInfo.getId()));
        }
        map.put("comeTime", parkingInfo.getComeTime());
        if(parkingInfo.getOutTime()!=null){
            map.put("outTime", parkingInfo.getOutTime());
        }
//        map.put("lotName", parkingInfo.getLotName());
//        map.put("carImg", String.valueOf(parkingInfo.getCarImg()));
        map.put("carId", String.valueOf(parkingInfo.getCarId()));
        try {
            return HttpUtil.sendPost3(Constants.URL_POST_PARKING_INFO, map);
        } catch (IOException e) {
            LOG.error(e.getMessage(),e);
            HttpResponse response=new HttpResponse();
            response.setCode(0);
            return response;
        }


    }

    public ParkingInfo requestParkingInfo(String carNo) {
        LOG.debug("getParkingInfo");
        System.setProperty("http.proxyHost", "cn-proxy.jp.oracle.com");
        System.setProperty("http.proxyPort", "80");
        StringBuilder sb = new StringBuilder();
        sb.append("https://cloud.keytop.cn/service/parking/queryWithLotId?lotId=2870&lpn=")
                .append(carNo);
        String url = sb.toString();
        try {
            HttpResponse response = HttpUtil.sendGet(url);
            GetParamResult result = JsonUtil.jsonToObj(response.getContent(), GetParamResult.class);
            if ("success".equals(result.getType())) {
                String parkingQueryUrl = Constants.URL_PARING_QUERY + result.getData().getParams().split("&")[4];
                response = HttpUtil.sendGet(parkingQueryUrl);
                GetParkingInfoResult parkingInfoResult = JsonUtil.jsonToObj(response.getContent(), GetParkingInfoResult.class);
                return parkingInfoResult.toParkingInfo();
            } else {
                LOG.info(result.getTip());
                ParkingInfo parkingInfo = new ParkingInfo();
                return parkingInfo;
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public static HttpResponse sendGet(String url) throws IOException {
        String result = "";
        HttpResponse response = new HttpResponse();
        URL realURL = new URL(url);
        URLConnection conn = realURL.openConnection();
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
        conn.setRequestProperty("referer", "https://cloud.keytop.cn/page/parking/lot_parking_query.html?lotId=2870" +
                "&lpn=%E8%8B%8FE12RG8&callbackUrl=https%3A%2F%2Fcloud.keytop.cn%2Fpage%2Fuser%2Flpn%2Flpn_bind_v2.html%3Fsource%3D3%26lotId%3D2870&source=3");
        conn.connect();
        Map<String, List<String>> map = conn.getHeaderFields();
        for (String s : map.keySet()) {
//            System.out.println(s + "-->" + map.get(s));
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        String line;
        while ((line = in.readLine()) != null) {
            result += "\n" + line;
        }
        response.setContent(result);

        return response;
    }


}
