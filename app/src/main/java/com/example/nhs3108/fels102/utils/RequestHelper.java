package com.example.nhs3108.fels102.utils;

import com.example.nhs3108.fels102.constants.HttpStatusConsts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
        int responseStatusCode;
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
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(data);
        wr.flush();
        wr.close();
        responseStatusCode = connection.getResponseCode();
        InputStream inputStream = (responseStatusCode >= HttpStatusConsts.OK
                && responseStatusCode < HttpStatusConsts.BAD_REQUEST) ?
                connection.getInputStream() : connection.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            break;
        }
        response = new ResponseHelper(connection.getResponseCode(), sb.toString());
        return response;
    }
}
