package app.ahreum.com.pacecounters.util;

/**
 * Created by ahreum on 2016-12-09.
 */

// 네이버 지도 API 예제 - 주소좌표변환
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import app.ahreum.com.pacecounters.ui.FragmentForMonitorScreen;


public  class APIExamMapGeocode extends AsyncTask<Integer,Integer,Integer>{ //naver개발자 사이트 예제파일

    String clientId = PaceCounterConst.MAP_API_KEY;//application client id
    String clientSecret = PaceCounterConst.MAP_API_KEY;//application client secret";
    FragmentForMonitorScreen mFragmentMonitor ;
    private String locationCode = "127.1052133,37.3595316"; //example location from naver
    private String result;
    public APIExamMapGeocode(FragmentForMonitorScreen frm){
        mFragmentMonitor = frm;
    }
    private String request(){
        StringBuffer response = new StringBuffer();
        try {
            //String addr = URLEncoder.encode("불정로 6", "utf-8");
            //String apiURL = "https://openapi.naver.com/v1/map/geocode.xml?query=" + addr; // 주소 -> 좌표 xml
            String apiURL = "https://openapi.naver.com/v1/map/reversegeocode.xml?encoding=utf-8&coordType=latlng&query=" + locationCode; // 좌표 ->주소 xml

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
            System.out.println(e);
        }
        return  getAddress(response);
        }
    private String getAddress(StringBuffer response){
        return  response.substring(response.indexOf("address"), response.indexOf("addrdetail"));
    }

    @Override
    protected void onPostExecute(Integer integer) {
        PaceCounterUtil.address = result;
        if(mFragmentMonitor.mTvLocation !=null) mFragmentMonitor.mTvLocation.setText(result);
    }
    @Override
    protected Integer doInBackground(Integer... integers) {
        result = request();
        return null;
    }
    public void setLocationCode(String longitude){
        locationCode = longitude;
    }
}