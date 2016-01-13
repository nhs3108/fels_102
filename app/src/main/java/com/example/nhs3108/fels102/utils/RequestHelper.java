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
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by nhs3108 on 1/4/16.
 */
public class RequestHelper {
    public static ResponseHelper executeRequest(String link, Method method, NameValuePair... args) throws IOException {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(Arrays.asList(args));
        return executeRequest(link, method, nameValuePairs);
    }

    public static ResponseHelper executeRequest(String link, Method method, ArrayList<NameValuePair> args) throws IOException {
        ResponseHelper response;
        int responseStatusCode;
        HttpURLConnection connection = null;
        if (method != Method.GET) {
            URL url = new URL(link);
            String charset = "UTF-8";
            String data = "";
            data = String.format("%s=%s",
                    URLEncoder.encode(args.get(0).getName(), charset),
                    URLEncoder.encode(args.get(0).getValue(), charset));
            for (NameValuePair nvp : args) {
                data += String.format("&%s=%s",
                        URLEncoder.encode(nvp.getName(), charset),
                        URLEncoder.encode(nvp.getValue(), charset));
            }
            connection = (HttpURLConnection) url.openConnection();
            if (method == Method.PATCH) {
                connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            }
            connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(data);
            wr.flush();
            wr.close();
        } else {
            link += String.format("?%s=%s", args.get(0).getName(), args.get(0).getValue());
            for (NameValuePair nvp : args) {
                link += String.format("&%s=%s", nvp.getName(), nvp.getValue());
            }
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
        }

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

    public enum Method {POST, GET, PATCH}
}
