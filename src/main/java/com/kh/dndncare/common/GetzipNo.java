package com.kh.dndncare.common;


import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;

public class GetzipNo {
	

	public static String ApiExplorer(String addBefor){
	        StringBuilder sb = new StringBuilder();
	        
	        try {
	        	 StringBuilder urlBuilder = new StringBuilder("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll"); /*URL*/
	        	 urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + URLEncoder.encode("AcjBypl1iy8gVIWLl%2B%2FmdUCYl0DqiUIxQrZbFaQzHNOUyRlcWEM88%2BNaOzYz2r6B9TBYowERnVXRq19kr%2Fwzyw%3D%3D", "UTF-8")); /*검색어*/
				/*Service Key*/
		        urlBuilder.append("&" + URLEncoder.encode("srchwrd","UTF-8") + "=" + URLEncoder.encode(addBefor, "UTF-8")); /*검색어*/
		        urlBuilder.append("&" + URLEncoder.encode("countPerPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지당 출력될 개수를 지정(최대50)*/
		        urlBuilder.append("&" + URLEncoder.encode("currentPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*출력될 페이지 번호*/
		        URL url = new URL(urlBuilder.toString());
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setRequestMethod("GET");
		        conn.setRequestProperty("Content-type", "application/json");
		        BufferedReader rd;
		        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
		            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		        } else {
		            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		        }
		        String line;
		        while ((line = rd.readLine()) != null) {
		            sb.append(line);
		        }
		        rd.close();
		        conn.disconnect();
	        
	        
			} catch (Exception e) {
				e.printStackTrace();
			} 
	        
	        return sb.toString();
	}
	
	
	

}
