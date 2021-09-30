package com.qa.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * http请求工具类
 */
@Slf4j
public class HttpUtils {

    private static final String DEFAULT_CHARSET = "UTF-8";
    private CloseableHttpClient httpclient;
    private CookieStore cookieStore;
    private HttpClientContext localContext;
    private String charset;
    private static final String FILE_COLUMN = "fileColumn";

    public HttpUtils() {
        httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        cookieStore = new BasicCookieStore();
        localContext = HttpClientContext.create();
        // Bind custom cookie store to the local context
        localContext.setCookieStore(cookieStore);
        this.charset = DEFAULT_CHARSET;
    }

    public HttpUtils(String charset) {
        httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        cookieStore = new BasicCookieStore();
        localContext = HttpClientContext.create();
        // Bind custom cookie store to the local context
        localContext.setCookieStore(cookieStore);
        this.charset = charset;
    }

    public void closeHttpClient() {
        IOUtils.closeQuietly(httpclient);
    }

    public String doGet(String url){
        return this.doGet(url, null, null);
    }

    public String doGet(String url, Map<String, String> params){
        return this.doGet(url, params, null);
    }

    public String doGet(String url, Map<String, String> params, Map<String, String> headers){
        try{
            if (MapUtils.isNotEmpty(params)) {
                URIBuilder uriBuilder = new URIBuilder(url);
                Set<String> keySet = params.keySet();
                for (String key : keySet) {
                    uriBuilder.addParameter(key, params.get(key));
                }
                url = uriBuilder.build().toString();
            }
            log.info("http get url={}",url);
            HttpGet httpget = new HttpGet(url);
            setHeaders(headers, httpget);
            log.info("http get header={}",headers.toString());
            CloseableHttpResponse response = httpclient.execute(httpget, localContext);
            return this.getResult(response);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            this.closeHttpClient();
        }
        return null;
    }


    public String doPost(String url, Map<String, String> params){
        HttpPost httpPost = new HttpPost(url);
        log.info("http post url={}",url);
        return this.doModify(httpPost, params);
    }

    /**
     * 根据不同的ContentType提交请求
     */
    public String postVariable(String url, String str, String contentType, String charset){
        try{
            HttpPost post = new HttpPost(url);
            log.info("http post url={}",url);
            StringEntity entity = new StringEntity(str, charset);
            log.info("http post entity={}",str);
            entity.setContentType(contentType);
            entity.setContentEncoding(charset);
            post.setEntity(entity);
            CloseableHttpResponse response = httpclient.execute(post);
            return getResult(response);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            this.closeHttpClient();
        }
        return null;
    }

    public String doPut(String url, Map<String, String> params){
        HttpPut httpPut = new HttpPut(url);
        log.info("http put url={}",url);
        return this.doModify(httpPut, params);
    }

    private String doModify(HttpEntityEnclosingRequestBase requestBase, Map<String, String> params){
        try{
            List<NameValuePair> parametersList = new ArrayList<NameValuePair>();

            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                NameValuePair parameters = new BasicNameValuePair(key, params.get(key));
                parametersList.add(parameters);
            }
            log.info("http post params={}",params.toString());
            requestBase.setEntity(new UrlEncodedFormEntity(parametersList, DEFAULT_CHARSET));

            CloseableHttpResponse response = httpclient.execute(requestBase, localContext);

            final List<Cookie> cookies = cookieStore.getCookies();
            for (Cookie cookie : cookies) {
                log.info(cookie.getName() + ":" + cookie.getValue());
            }

            return this.getResult(response);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            this.closeHttpClient();
        }
        return null;
    }

    public static String getResult(CloseableHttpResponse response) throws ParseException, IOException {
        try {
            HttpEntity entity = response.getEntity();
            String result = entity != null ? EntityUtils.toString(entity, DEFAULT_CHARSET) : null;
            EntityUtils.consume(entity);
            log.info("http response={}",result);
            return result;
        } finally {
            IOUtils.closeQuietly(response);
        }
    }

    public String doPostFile(String url, File file) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        log.info("http post url={}",url);
        return this.doModifyFile(httpPost, file);
    }

    private String doModifyFile(HttpEntityEnclosingRequestBase requestBase, File file)
            throws ParseException, IOException {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder = multipartEntityBuilder.addBinaryBody(FILE_COLUMN, file);
        HttpEntity httpEntity = multipartEntityBuilder.build();
        requestBase.setEntity(httpEntity);
        CloseableHttpResponse response = httpclient.execute(requestBase, localContext);
        return this.getResult(response);
    }

    /**
     * 设置头信息
     *
     * @param headers
     * @param httpRequestBase
     */
    private static void setHeaders(Map<String, String> headers, HttpRequestBase httpRequestBase) {
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpRequestBase.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }
}
