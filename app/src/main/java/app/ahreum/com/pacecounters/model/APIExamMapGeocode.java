package app.ahreum.com.pacecounters.model;

/**
 * Created by ahreum on 2016-12-09.
 *change thread to AsyncTask 1)to change Textview and 2)use one AsyncTask at a onetime
 */

// 네이버 지도 API 예제 - 주소좌표변환
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public  class APIExamMapGeocode extends AsyncTask<String, Void, String>{ //naver개발자 사이트 예제파일
    private TextView adressText;
    private String clientId = PaceCounterConst.MAP_API_KEY;//application client id
    private String clientSecret = PaceCounterConst.MAP_SECRET_KEY;//application client secret";

    private String result;

    public APIExamMapGeocode(TextView tv){
        adressText = tv;
    }

    private String request(String locationCode){
       // StringBuffer response = new StringBuffer();  //thread safe but doesn't need for this function. change to StringBuilder
        StringBuilder response = new StringBuilder();
        response.length();
        try {
            //String addr = URLEncoder.encode("불정로 6", "utf-8");
            //String apiURL = "https://openapi.naver.com/v1/map/geocode.xml?query=" + addr; // 주소 -> 좌표 xml
            String apiURL =  "https://openapi.naver.com/v1/map/reversegeocode?encoding=utf-8&coordType=latlng&query="+ checkLocation(locationCode); // 좌표 ->주소 xml
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            int responseCode = con.getResponseCode();

            BufferedReader br;
            if (responseCode == 200) { // success
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // error
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                 response.append(inputLine);
            }
            br.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return  getAddress(response);
        }
    private String getAddress(StringBuilder response){
        String sido =response.substring(response.indexOf("sido")+8, response.indexOf("sigugun") -23);;
        String sigugun=response.substring(response.indexOf("sigugun")+11, response.indexOf("dongmyun") -23);
        String dongmyun=response.substring(response.indexOf("dongmyun")+12, response.indexOf("rest") -23);
        result =  sido + sigugun + dongmyun;
        return  result;
    }
    private String checkLocation(String str){
        if(str == null){
           return  "127.1052133,37.3595316"; //example location from naver
        }else{
            return str;
        }
    }
    @Override
    protected void onPostExecute(String s) {
        adressText.setText(result);
    }

    @Override
    protected String doInBackground(String... str) {
        return request(str[0]);
    }

}