package app.ahreum.com.pacecounters.util;

/**
 * Created by ahreum on 2016-12-09.
 */

// 네이버 지도 API 예제 - 주소좌표변환
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public  class APIExamMapGeocode extends Thread{ //naver개발자 사이트 예제파일
        private boolean mRunning = false;
        @Override
        public void run() {
        String clientId = PaceCounterConst.MAP_API_KEY;//애플리케이션 클라이언트 아이디값";
        String clientSecret = PaceCounterConst.MAP_API_KEY;//애플리케이션 클라이언트 시크릿값";

        String apiURL =  "https://openapi.naver.com/v1/map/reversegeocode?encoding=utf-8&coord=latlng&output=json&query=";
        //"https://openapi.naver.com/v1/map/geocode?query=" + addr; //json
        //String apiURL = "https://openapi.naver.com/v1/map/geocode.xml?query=" + addr; // xml

            while (mRunning) {
                try {
                    String addr = URLEncoder.encode("불정로 6", "UTF-8");
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                    Log.d("ahreum", "APIExamMapGeocode");
                    int responseCode = con.getResponseCode();
                    BufferedReader br;
                    if (responseCode == 200) { // 정상 호출
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {  // 에러 발생
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                    PaceCounterUtil.address = response.toString();
                    mRunning = false;
                    //   System.out.println(response.toString());
                } catch (Exception e) {
                    //   PaceCounterUtil.showToast(context, context.getResources().getString(R.string.toast_msg_cannot_get_location));
                    System.out.println(e);
                }
            }
         }
        public void close() {
            mRunning = false;
        }
}