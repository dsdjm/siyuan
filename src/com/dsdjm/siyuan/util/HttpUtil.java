package com.dsdjm.siyuan.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HttpUtil {

    public static String get(String uri) throws ClientProtocolException,
            IOException {
        HttpClient httpClient = new DefaultHttpClient();
        // ContentEncodingHttpClient httpClient = new ContentEncodingHttpClient();
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        int statusCode = httpResponse.getStatusLine().getStatusCode();

        if (statusCode >= 200 && statusCode < 400) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpResponse.getEntity().getContent(), "UTF-8"));
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                stringBuilder.append(s);
            }
            reader.close();
            LogUtil.println("HTTP GET:" + uri.toString());
            LogUtil.println("Response:" + stringBuilder.toString());
            return stringBuilder.toString();
        }
        return null;
    }


    public static String post(String uri, List<NameValuePair> formparams)
            throws IOException {

        HttpClient httpClient = new DefaultHttpClient();
        // ContentEncodingHttpClient httpClient = new
        // ContentEncodingHttpClient();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
                "UTF-8");
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpClient.execute(httpPost);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 400) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpResponse.getEntity().getContent(), "UTF-8"));
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                stringBuilder.append(s);
            }
            reader.close();
            LogUtil.println("HTTP POST:" + uri.toString());
            LogUtil.println("Response:" + stringBuilder.toString());
            return stringBuilder.toString();
        }
        return null;
    }

    public static String post(String actionUrl, List<NameValuePair> formparams,
                              List<File> attachments) throws ClientProtocolException, IOException {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";

        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000); // 缓存的最长时间
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                + ";boundary=" + BOUNDARY);

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        if (formparams != null) {
            for (NameValuePair entry : formparams) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\""
                        + entry.getName() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET
                        + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue());
                sb.append(LINEND);
            }
        }

        DataOutputStream outStream = new DataOutputStream(
                conn.getOutputStream());
        outStream.write(sb.toString().getBytes());
        // 发送文件数据
        if (attachments != null) {
            int i = 0;
            for (File file : attachments) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"file"
                        + (i++) + "\"; filename=\"" + file.getName() + "\""
                        + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());

                InputStream is = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }
        }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        // 得到响应码
        int statusCode = conn.getResponseCode();
        InputStream in = null;
        if (statusCode >= 200 && statusCode < 400) {
            in = conn.getInputStream();
            int ch;
            StringBuilder stringBuilder = new StringBuilder();
            while ((ch = in.read()) != -1) {
                stringBuilder.append((char) ch);
            }
            LogUtil.println("HTTP POST:" + uri.toString());
            LogUtil.println("Response:" + stringBuilder.toString());
            return stringBuilder.toString();
        }
        return in == null ? null : in.toString();
    }


    public static boolean download(File file, String uri) throws IOException {
        if (file.exists() && file.isDirectory()) {
            return false;
        }
        boolean flag = false;
        file.getParentFile().mkdirs();
        File tmpFile = new File(file.getAbsolutePath() + ".tmp");
        InputStream is = null;
        BufferedOutputStream bos = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            // ContentEncodingHttpClient httpClient = new
            // ContentEncodingHttpClient();
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                tmpFile.createNewFile();
                is = httpResponse.getEntity().getContent();
                bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
                byte[] buffer = new byte[4096];
                int i = is.read(buffer);
                while (i != -1) {
                    bos.write(buffer, 0, i);
                    i = is.read(buffer);
                }
                tmpFile.renameTo(file);
                flag = true;
            }
        } catch (IOException ioe) {
            LogUtil.exception(ioe.getMessage());
            throw ioe;
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (is != null) {
                is.close();
            }
        }

        return flag;
    }


}
