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
	        	StringBuilder urlBuilder = new StringBuilder("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdService/retrieveNewAdressAreaCdService/getNewAddressListAreaCd"); /*URL*/
	            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=AcjBypl1iy8gVIWLl%2B%2FmdUCYl0DqiUIxQrZbFaQzHNOUyRlcWEM88%2BNaOzYz2r6B9TBYowERnVXRq19kr%2Fwzyw%3D%3D"); /*Service Key*/
	            urlBuilder.append("&" + URLEncoder.encode("searchSe","UTF-8") + "=" + URLEncoder.encode("road", "UTF-8")); /*dong : 동(읍/면)명road :도로명[default]post : 우편번호*/
	            urlBuilder.append("&" + URLEncoder.encode("srchwrd","UTF-8") + "=" + URLEncoder.encode(addBefor, "UTF-8")); /*검색어*/
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
	        
	        System.out.println(sb.toString());
	        return sb.toString();
	}
	
	
	

}
