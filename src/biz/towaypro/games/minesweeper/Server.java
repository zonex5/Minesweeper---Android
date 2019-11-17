package biz.towaypro.games.minesweeper;

import android.content.Context;
import android.net.ConnectivityManager;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class Server
{
    static public HttpClient getHttpClient()
    {
        String proxyHost = android.net.Proxy.getDefaultHost();
        int proxyPort = android.net.Proxy.getDefaultPort();
        HttpClient httpClient = new DefaultHttpClient();
        if(proxyPort > 0)
        {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        return httpClient;
    }

    static public Boolean executeQueryGET(String url)
    {
        boolean result = true;
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = getHttpClient();
        try
        {
            HttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            {
                result = false;
            }
        }
        catch(Exception e)
        {
            result = false;
        }
        return result;
    }

    static public String executeQueryGETForResult(String url)
    {
        StringBuilder sb = new StringBuilder();
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = getHttpClient();
        try
        {
            HttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }
            }
        }
        catch(Exception e)
        {
            return "";
        }
        return sb.toString();
    }

    static public String executeQueryPOSTForResult(String url, ArrayList<NameValuePair> params)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }
            }
        }
        catch(Exception e)
        {
            return "";
        }
        return sb.toString();
    }

    static public boolean executeQueryPOST(String url, ArrayList<NameValuePair> params)
    {
        boolean result = true;
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            {
                result = false;
            }
        }
        catch(Exception e)
        {
            result = false;
        }
        return result;
    }

    static public boolean isOnline(Context systemContext)
    {
        ConnectivityManager cm = (ConnectivityManager) systemContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    static public String getUniqueID()
    {
        return executeQueryGETForResult("http://toway.biz/minesweeper/pro/getid.php");
    }
}
