package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.TranslationRequest;
import com.example.demo.model.TranslationResponse;

@RestController
@RequestMapping("/api")
public class TranslationController {

    @PostMapping("/translate")
    public TranslationResponse translateText(@RequestBody TranslationRequest request) {
    	TranslationResponse ansObj = new TranslationResponse();
    	try {
    		String text = request.getText();
        	String sourceLang = "en";
            String targetLang = "fr";
            String encodedSourceText = URLEncoder.encode(text, "UTF-8");
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang +
                    "&tl=" + targetLang + "&dt=t&q=" + encodedSourceText;
            
            URL obj = new URL(url);
            
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            con.setRequestMethod("GET");
            
            int responseCode = con.getResponseCode();
            
           if(responseCode == 200) {
               BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
               String inputLine;
               StringBuffer response = new StringBuffer();
               
               while ((inputLine = in.readLine()) != null) {
                   response.append(inputLine);
               }
               in.close();
               
               String translatedText = parseTranslationResponse(response.toString());
               
               ansObj.setTranslation(translatedText);
           }else {
        	   ansObj.setTranslation("Error occured!!");
           }
        	
		} catch (Exception e) {
			System.out.println("Error in your api");
		}
    	
        return ansObj;
    }

    private static String parseTranslationResponse(String jsonResponse) {
    	try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            JSONArray translationArray = jsonArray.getJSONArray(0);
            JSONArray translationItemArray = translationArray.getJSONArray(0);
            return translationItemArray.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
            return "Translation not found";
        }
    }
}

