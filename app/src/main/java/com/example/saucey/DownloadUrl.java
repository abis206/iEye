package com.example.saucey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl {

    public String readURL(String myURL) throws IOException {
          String data = "";
        InputStream inputStream = null;
        HttpURLConnection urLConnection = null;
        try {
            URL url = new URL(myURL);
            urLConnection = (HttpURLConnection) url.openConnection();
            urLConnection.connect();
            inputStream = urLConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            urLConnection.disconnect();
        }
        return data;
    }

}
