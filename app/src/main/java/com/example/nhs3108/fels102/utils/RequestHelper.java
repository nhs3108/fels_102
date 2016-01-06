package com.example.nhs3108.fels102.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by nhs3108 on 1/4/16.
 */
public class RequestHelper {
    public static ResponseHelper executePostRequest(String link, NameValuePair... args) throws IOException {
        ResponseHelper response;
        URL url = new URL(link);
        String charset = "UTF-8";
        String data = "";
        data = String.format("%s=%s",
                URLEncoder.encode(args[0].getName(), charset),
                URLEncoder.encode(args[0].getValue(), charset));
        for (int i = 1; i < args.length; i++) {
            data += String.format("&%s=%s",
                    URLEncoder.encode(args[i].getName(), charset),
                    URLEncoder.encode(args[i].getValue(), charset));
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        try {
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            response = new ResponseHelper(connection.getResponseCode(), sb.toString());
        } catch (FileNotFoundException e) {
            response = new ResponseHelper(connection.getResponseCode(), "");
        }
        return response;
    }
}
