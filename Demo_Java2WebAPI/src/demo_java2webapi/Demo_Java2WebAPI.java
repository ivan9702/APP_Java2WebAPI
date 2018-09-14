/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo_java2webapi;
import java.net.URL;
import java.net.HttpURLConnection;
import java.security.cert.Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManager;
//import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;

import java.net.MalformedURLException;
import java.io.*;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

/**
 *
 * @author lucky
 */
public class Demo_Java2WebAPI {
    
    public static int UI_FP_Index = 1;
    public static String UI_User_ID = "035X";
    public static Boolean UI_HTTPS_Enable = true;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

         do_Enroll();
    }
    private static void do_Load_FP_Service()
    {
        WebAPI_load_fp_srv();
    }
        
    private static void do_Enroll()
    {
        WebAPI_get_enroll_minutiae();
    }
    
    private static void do_Identify()
    {
        WebAPI_get_minutiae();
    }
    
    private static void do_Verify()
    {
        WebAPI_get_minutiae();
    }
    
    private static void do_Delete()
    {
        WebAPI_get_delete_data();
    }
    
    private static void do_Set_Session_key()
    {
        WebAPI_set_session_key();
    }
    
    private static void WebAPI_load_fp_srv()
    {
        Boolean https_en = UI_HTTPS_Enable ; //checkBox_https.Enabled;
        String route = "/api/load_fp_srv";
        Boolean IgnoreCA = true;
        String json_out = PostJson2WebAPI(https_en, route, "", IgnoreCA);
    }

    private static String WebAPI_get_minutiae()
    {
        Boolean https_en = UI_HTTPS_Enable; //checkBox_https.Enabled;
        String route = "/api/get_minutiae";
        Boolean IgnoreCA = true;
        String ret = PostJson2WebAPI(https_en, route, "", IgnoreCA);

        return ret;
    }
    
    //this function is specified session key for Startek format "2018-08-20-14-50-00           "
    private static String WebAPI_set_session_key()
    {
        Boolean https_en = UI_HTTPS_Enable;// checkBox_https.Enabled;
        String route = "/api/set_session_key ";

        //modify key as you need
        String startek_sessionkey = StartekSessionKey();

        String json_str = "{sessionkey:\""+ startek_sessionkey  + "\"}";
        Boolean IgnoreCA = true;
        String ret = PostJson2WebAPI(https_en, route, json_str, IgnoreCA);

        return ret;
    }

    private static String WebAPI_get_enroll_minutiae()
    {
        Boolean https_en = UI_HTTPS_Enable ; //checkBox_https.Enabled;
        String route = "/api/get_enroll_minutiae";
        Boolean IgnoreCA = true;
        String ret = PostJson2WebAPI(https_en, route, "", IgnoreCA);

        return ret;
    }
        
    private static String WebAPI_get_delete_data()
    {
        Boolean https_en = UI_HTTPS_Enable; //checkBox_https.Enabled;
        String id = UI_User_ID ; //richTextBox_id.Text;
        int fp_idx = UI_FP_Index;
        String route = "/api/get_delete_data";
        String json_str = "{clientUserId:\"" + id + "\", fpIndex:" + Integer.toString(fp_idx)+ "}";
        Boolean IgnoreCA = true;
        String ret = PostJson2WebAPI(https_en, route, json_str, IgnoreCA);

        return ret;
    }
        
    private static String PostJson2WebAPI(boolean  https_en, String route ,String json_string, Boolean Ignore_CA)
    {
        String protocol = "";
        String ret_str = "";
        
        if(https_en == true)
        {
            protocol = "https://localhost:5888";
        }
        else
        {
            protocol = "http://localhost:5887";
        }
        
        //HttpURLConnection connection = null; 
         DataOutputStream wr;
         InputStream is;
        try
        {
            URL url = new URL(protocol + route);
            if(https_en == true)
            {
                if(Ignore_CA == true)   //if need to ignore CA (ex. self signed CA for HTTPS)
                {
                    TrustManager[] trustAllCerts = new TrustManager[] { 
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                                return new X509Certificate[0];
                            } 
                            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                            } 
                            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        } 
                    }; 
                
                    SSLContext sc = SSLContext.getInstance("SSL"); 
                    sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                }

                
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(json_string.length())); //?
                connection.setRequestProperty("User-agent","myapp");
                connection.setUseCaches(false); 
                connection.setDoOutput(true);
                connection.setDoInput(true);
            
                //Write out
                wr = new DataOutputStream (connection.getOutputStream ());
                is = connection.getInputStream();
            }
            else
            {
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(json_string.length())); //?
                connection.setRequestProperty("User-agent","myapp");
                connection.setUseCaches(false); 
                connection.setDoOutput(true);
                connection.setDoInput(true);
                //Write out
                wr = new DataOutputStream (connection.getOutputStream ());
                is = connection.getInputStream();
            }
 
            wr.writeBytes (json_string);
            wr.flush ();
            wr.close ();
            
            
            //Get Response	
            //InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer(); 
            while((line = rd.readLine()) != null) 
            {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            ret_str = response.toString();
            //return response.toString();
            
            
        }
        catch(Exception e) 
        {
            
        }
        finally 
        {
            //if(connection != null) 
            {
                //connection.disconnect(); 
                
            }
        }    
        return ret_str;
    }
    private static String PostJson2RedirectServer(boolean https_en,String SrvIp, String port, String route, String json_string, Boolean Ignore_CA)
    {
        String protocol = "";
        String ret_str = "";
        
        if(https_en == true)
        {
            protocol = "https://" + SrvIp + ":" + port;
        }
        else
        {
            protocol = "http://"+ SrvIp + ":" + port;
        }
        
        //HttpURLConnection connection = null; 
         DataOutputStream wr;
         InputStream is;
        try
        {
            URL url = new URL(protocol + route);
            if(https_en == true)
            {
                if(Ignore_CA == true)   //if need to ignore CA (ex. self signed CA for HTTPS)
                {
                    TrustManager[] trustAllCerts = new TrustManager[] { 
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                                return new X509Certificate[0];
                            } 
                            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                            } 
                            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        } 
                    }; 
                
                    SSLContext sc = SSLContext.getInstance("SSL"); 
                    sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                }

                
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(json_string.length())); //?
                connection.setRequestProperty("User-agent","myapp");
                connection.setUseCaches(false); 
                connection.setDoOutput(true);
                connection.setDoInput(true);
            
                //Write out
                wr = new DataOutputStream (connection.getOutputStream ());
                is = connection.getInputStream();
            }
            else
            {
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(json_string.length())); //?
                connection.setRequestProperty("User-agent","myapp");
                connection.setUseCaches(false); 
                connection.setDoOutput(true);
                connection.setDoInput(true);
                //Write out
                wr = new DataOutputStream (connection.getOutputStream ());
                is = connection.getInputStream();
            }
 
            wr.writeBytes (json_string);
            wr.flush ();
            wr.close ();
            
            
            //Get Response	
            //InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer(); 
            while((line = rd.readLine()) != null) 
            {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            ret_str = response.toString();
            //return response.toString();
            
            
        }
        catch(Exception e) 
        {
            
        }
        finally 
        {
            //if(connection != null) 
            {
                //connection.disconnect(); 
                
            }
        }    
        return ret_str;
    }
        
    private static String StartekSessionKey()
    {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String str_pad_blank = padRight(dateFormat.format(date), 32);
        char[] charValues = str_pad_blank.toCharArray();
        String hexOutput = "";
        for (int i = 0; i < charValues.length; i++) 
        {
            hexOutput = hexOutput + String.format("%02X", (int) charValues[i]);
        }  

        return hexOutput;

    }
    
    public static String padRight(String s, int n) 
    {
        return String.format("%1$-" + n + "s", s);  
    }

    public static String padLeft(String s, int n) 
    {
        return String.format("%1$" + n + "s", s);  
    }
}
