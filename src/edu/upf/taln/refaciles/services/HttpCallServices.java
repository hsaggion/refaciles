/*
 * A bit of code to call the external services
 */
package edu.upf.taln.refaciles.services;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

/**
 *
 * @author UPF
 */
public class HttpCallServices {
    
  


	private final String USER_AGENT = "Mozilla/5.0";
        
        // example of use 
	public static void main(String[] args) throws Exception {
                String simpleText;
		HttpCallServices http = new HttpCallServices();
                
          //      System.out.println(simpleText);HttpCallServices http = new HttpCallServices();

		System.out.println("Calling the Simplifier");
                String sentence;
                sentence="Los facultativos estudian medicina.";
                String response=http.sendGetSpeech(sentence, "es");
                System.out.println(response);
                JSONObject jsonSpeech=new JSONObject(response); 
                System.out.println(jsonSpeech.get("audioSpeech"));
                
               


	}

        // able to include & taln services
        // simplifier
        public static String esSimplifier="http://simplext.taln.upf.edu/api/simplify/text?callback=callback&text=";
        public static String enSimplifier="http://able2include.taln.upf.edu/api/simplify/text?callback=callback&text=";
        // text to speech
        public static String speech="http://al.abletoinclude.eu/Text2Speech.php?";
        
        public void downloadSpeech(String url, String ref) {
          
            try {
             OutputStreamWriter osw;
     
             File fout=new File("."+File.separator+"src"+File.separator+"utils"+File.separator+ref+".mp4");
           
             FileOutputStream writer=new FileOutputStream(fout);
                URL obj = new URL(url);
                
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                
                
                
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                
                while ((inputLine = in.readLine()) != null) {
                   
                }
                in.close();
            } catch (MalformedURLException ex) {
                Logger.getLogger(HttpCallServices.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HttpCallServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public String sendGetSpeech(String sentence, String language) 
                throws Exception {


                String url;
                if(language.equals("es")) {
                    url=speech+"text="+URLEncoder.encode(sentence, "UTF-8")+"&"+"language=spanish";
                } else {
                    url=speech+"text="+URLEncoder.encode(sentence, "UTF-8")+"&"+"language=english";;
                    
                }
		URL obj = new URL(url);
                
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                
		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Charset", "UTF-8");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		//System.out.println(response.toString());
                
                return response.toString();

	}
        // HTTP GET request
	public String sendGet(String sentence,String language) throws Exception {


                String url;
                if(language.equals("es")) {
                    url=esSimplifier+URLEncoder.encode(sentence, "UTF-8");
                } else {
                    url=enSimplifier+URLEncoder.encode(sentence, "UTF-8");
                    
                }
		URL obj = new URL(url);
                
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                
		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Charset", "UTF-8");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		//System.out.println(response.toString());
                
                return response.toString();

	}

	

}