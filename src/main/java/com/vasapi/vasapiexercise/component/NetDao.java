package com.vasapi.vasapiexercise.component;

import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class NetDao {
    public String request(String endpoint) throws Exception {
        StringBuilder sb = new StringBuilder();

        URL url = new URL(endpoint);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            // read in the bytes
            InputStream inputStream = urlConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            // read them as characters.
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // read one line at a time.
            String inputLine = bufferedReader.readLine();
            while (inputLine != null) {
                // add this to our output
                sb.append(inputLine);
                // reading the next line
                inputLine = bufferedReader.readLine();
            }
        } finally {
            urlConnection.disconnect();
        }
        String withCommas = sb.toString().replaceAll("}", "},");
        String jsonWithoutBracket = removeLastCharacter(withCommas);
        return "[" + jsonWithoutBracket + "]";

    }

    public static String removeLastCharacter(String str) {
        String result = null;
        if ((str != null) && (str.length() > 0)) {
            result = str.substring(0, str.length() - 1);
        }
        return result;
    }
}
