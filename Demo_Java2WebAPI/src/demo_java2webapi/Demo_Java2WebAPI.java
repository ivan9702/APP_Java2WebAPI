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
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


import java.net.MalformedURLException;
import java.io.*;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

//use GSON as json parser
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonParseException;

/**
 *
 * @author lucky
 */
public class Demo_Java2WebAPI {
    
    public static int UI_FP_Index = 1;
    public static String UI_User_ID = "035X";
    public static Boolean UI_HTTPS_Enable = true;
    public static int UI_Privilege = 2;
    
    public static String UI_Srv_IP = "192.168.1.76";
    public static String UI_Srv_Port = "8444";  //8444 for https, 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

         do_Delete();
    }
    private static void do_Load_FP_Service()
    {
        WebAPI_load_fp_srv();
    }
        
    private static void do_Enroll()
    {
        String webapi_str = WebAPI_get_enroll_minutiae();
        String str_to_srv = ReComposeJson_Enroll(webapi_str);
        String results = Srv_Enroll(str_to_srv);
        System.out.println("Srv Return: " + results);
    }
    
    private static void do_Identify()
    {
        String webapi_str = WebAPI_get_minutiae();
        String str_to_srv = ReComposeJson_Identify(webapi_str);
        String results = Srv_Identify(str_to_srv);
        System.out.println("Srv Return: " + results);
    }
    
    private static void do_Verify()
    {
        String webapi_str = WebAPI_get_minutiae();
        String str_to_srv = ReComposeJson_Enroll(webapi_str);
        String results = Srv_Verify(str_to_srv);
        System.out.println("Srv Return: " + results);
    }
    
    private static void do_Delete()
    {
        String webapi_str = WebAPI_get_delete_data();
        String str_to_srv = ReComposeJson_Delete(webapi_str);
        String results = Srv_Delete(str_to_srv);
        System.out.println("Srv Return: " + results);
    }
    
    private static void do_Set_Session_key()
    {
        String webapi_str = WebAPI_set_session_key();
        System.out.println("WebAPI Return: " + webapi_str);
    }
    
    private static String WebAPI_load_fp_srv()
    {
        Boolean https_en = UI_HTTPS_Enable ; //checkBox_https.Enabled;
        String route = "/api/load_fp_srv";
        Boolean IgnoreCA = true;
        String ret = PostJson2WebAPI(https_en, route, "", IgnoreCA);
        
        return ret;
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
                    
                    HostnameVerifier allHostsValid = new HostnameVerifier() 
                    {
                        public boolean verify(String hostname, SSLSession session) 
                        {
                            return true;
                        };
                    };

                    // Install the all-trusting host verifier
                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                }

                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-length", Integer.toString(json_string.length())); //?
                connection.setRequestProperty("User-agent","myapp");
                connection.setUseCaches(false); 
                connection.setDoOutput(true);
                connection.setDoInput(true);
            
                //Write out
                wr = new DataOutputStream (connection.getOutputStream ());
                wr.writeBytes (json_string);
                wr.flush ();
                wr.close ();
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
                wr.writeBytes (json_string);
                wr.flush ();
                wr.close ();
                is = connection.getInputStream();

            }

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
           
        }
        catch(Exception e) 
        {
            System.out.println("ERROR: " + e);
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
    private static String ReComposeJson_Enroll(String json_in)
    {
        String ret = "";
        Gson gson_in = new Gson();
        json_get_minutiae json_obj_in = gson_in.fromJson(json_in, json_get_minutiae.class);
            
        try
        {
            json_srv_enroll json_obj_out = new json_srv_enroll();
            
            json_obj_out.set_clientUserId(UI_User_ID);// = UI_User_ID;
            json_obj_out.set_fpIndex(UI_FP_Index);// = UI_FP_Index;
            json_obj_out.set_privilege(UI_Privilege);// = UI_Privilege;
            json_obj_out.set_encMinutiae(json_obj_in.data.get_encMinutiae());// = json_obj_in.data.get_encMinutiae();
            json_obj_out.set_eSkey(json_obj_in.data.get_eSkey());// = json_obj_in.data.get_eSkey();
            json_obj_out.set_iv(json_obj_in.data.get_iv());// = json_obj_in.data.get_iv();
               
            Gson gson_out = new Gson();
            ret = gson_out.toJson(json_obj_out); 
         }
        catch(Exception e)
        {
              System.out.println("ERROR in ReComposeJson_Enroll(): " + e);
        }
        return ret;
    }
    
    private static String ReComposeJson_Identify(String json_in)
    {
        String ret = "";
        Gson gson_in = new Gson();
        json_get_minutiae json_obj_in = gson_in.fromJson(json_in, json_get_minutiae.class);
            
        try
        {
            json_srv_identify json_obj_out = new json_srv_identify();

            json_obj_out.set_encMinutiae(json_obj_in.data.get_encMinutiae());// = json_obj_in.data.get_encMinutiae();
            json_obj_out.set_eSkey(json_obj_in.data.get_eSkey());// = json_obj_in.data.get_eSkey();
            json_obj_out.set_iv(json_obj_in.data.get_iv());// = json_obj_in.data.get_iv();
               
            Gson gson_out = new Gson();
            ret = gson_out.toJson(json_obj_out); 
         }
        catch(Exception e)
        {
              System.out.println("ERROR in ReComposeJson_Identify(): " + e);
        }
        return ret;
    }
        
    private static String ReComposeJson_Delete(String json_in)
    {
        String ret = "";
        Gson gson_in = new Gson();
        json_get_delete_data json_obj_in = gson_in.fromJson(json_in, json_get_delete_data.class);
            
        try
        {
            json_srv_delete json_obj_out = new json_srv_delete();

            json_obj_out.set_clientUserId(json_obj_in.data.get_clientUserId());// = json_obj_in.data.get_encMinutiae();
            json_obj_out.set_deleteData(json_obj_in.data.get_deleteData());// = json_obj_in.data.get_eSkey();
               
            Gson gson_out = new Gson();
            ret = gson_out.toJson(json_obj_out); 
         }
        catch(Exception e)
        {
              System.out.println("ERROR in ReComposeJson_Delete(): " + e);
        }
        return ret;
    }
    
        private static String Srv_Enroll(String json_string)
        {
            Boolean https_en = UI_HTTPS_Enable;
            String ip = UI_Srv_IP;
            String port = UI_Srv_Port;
            String route = "/redirect/enroll";
            Boolean ignore_https_ca = true;
                    
            String ret_str = PostJson2RedirectServer(https_en,ip, port, route, json_string, ignore_https_ca);

            return ret_str;
        }

        private static String Srv_Verify(String json_string)
        {
            Boolean https_en = UI_HTTPS_Enable;
            String ip = UI_Srv_IP;
            String port = UI_Srv_Port;
            String route = "/redirect/verify";
            Boolean ignore_https_ca = true;

            String ret_str = PostJson2RedirectServer(https_en,ip, port, route, json_string, ignore_https_ca);

            return ret_str;
        }

        private static String Srv_Identify(String json_string)
        {
            Boolean https_en = UI_HTTPS_Enable;
            String ip = UI_Srv_IP;
            String port = UI_Srv_Port;
            String route = "/redirect/identify";
            Boolean ignore_https_ca = true;

            String ret_str = PostJson2RedirectServer(https_en,ip, port, route, json_string, ignore_https_ca);

            return ret_str;
        }

        private static String Srv_Delete(String json_string)
        {
            Boolean https_en = UI_HTTPS_Enable;
            String ip = UI_Srv_IP;
            String port = UI_Srv_Port;
            String route = "/redirect/delete";
            Boolean ignore_https_ca = true;

            String ret_str = PostJson2RedirectServer(https_en,ip, port, route, json_string, ignore_https_ca);

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
                    
                    HostnameVerifier allHostsValid = new HostnameVerifier() 
                    {
                        public boolean verify(String hostname, SSLSession session) 
                        {
                            return true;
                        };
                    };

                    // Install the all-trusting host verifier
                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                    
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
                wr.writeBytes (json_string);
                wr.flush ();
                wr.close ();
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
                wr.writeBytes (json_string);
                wr.flush ();
                wr.close ();
                is = connection.getInputStream();
            }
            /*
            wr.writeBytes (json_string);
            wr.flush ();
            wr.close ();

            */
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
            System.out.println("ERROr: " + e);
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
    
    public class json_data_get_minutiae {
        @SerializedName("encMinutiae")
        private String encMinutiae;
        public String get_encMinutiae() { return encMinutiae; }
        public void set_encMinutiae(String data) { this.encMinutiae = data;}

        @SerializedName("eSkey")
        private String eSkey;
        public String get_eSkey() { return eSkey; }
        public void set_eSkey(String data) { this.eSkey = data; }
        
        @SerializedName("iv")
        private String iv;
        public String get_iv() { return iv;}
        public void set_iv(String data) { this.iv = data; }
    }
    
    public class json_get_minutiae {
        @SerializedName("code")
        private String code;
        public String get_code() { return code;}
        public void set_code(String data) {this.code = data;}

        @SerializedName("message")
        private String message;
        public String get_message() { return message; }
        public void set_message(String data) { this.message = data; }
        
        @SerializedName("data")
        private json_data_get_minutiae data;
        public json_data_get_minutiae get_data() { return data; }
        public void set_data(json_data_get_minutiae data_in) { this.data = data_in; }
    }

    public static class json_set_session_key {
        @SerializedName("code")
        private String code;
        public String get_code() { return code;}
        public void set_code(String data) {this.code = data;}

        @SerializedName("message")
        private String message;
        public String get_message() { return message; }
        public void set_message(String data) { this.message = data; }
    }
    
    public static class json_data_get_delete_data {
        @SerializedName("clientUserId")
        private String clientUserId;
        public String get_clientUserId() { return clientUserId;}
        public void set_clientUserId(String data) {this.clientUserId = data;}

        @SerializedName("deleteData")
        private String deleteData;
        public String get_deleteData() { return deleteData; }
        public void set_deleteData(String data) { this.deleteData = data; }
    }
        
    public static class json_get_delete_data {
        @SerializedName("code")
        private String code;
        public String get_code() { return code;}
        public void set_code(String data) {this.code = data;}

        @SerializedName("message")
        private String message;
        public String get_message() { return message; }
        public void set_message(String data) { this.message = data; }
        
        @SerializedName("data")
        private json_data_get_delete_data data;
        public json_data_get_delete_data get_data() { return data; }
        public void set_data(json_data_get_delete_data data_in) { this.data = data_in; }
    }
    
    public static class json_srv_enroll {
        @SerializedName("encMinutiae")
        private String encMinutiae;
        public String get_encMinutiae() { return encMinutiae;}
        public void set_encMinutiae(String data) {this.encMinutiae = data;}

        @SerializedName("eSkey")
        private String eSkey;
        public String get_eSkey() { return eSkey; }
        public void set_eSkey(String data) { this.eSkey = data; }
        
        @SerializedName("iv")
        private String iv;
        public String get_iv() { return iv; }
        public void set_iv(String data) { this.iv = data; }
        
        @SerializedName("clientUserId")
        private String clientUserId;
        public String get_clientUserId() { return clientUserId; }
        public void set_clientUserId(String data) { this.clientUserId = data; }
        
        @SerializedName("fpIndex")
        private int fpIndex;
        public int get_fpIndex() { return fpIndex; }
        public void set_fpIndex(int data) { this.fpIndex = data; }
        
        @SerializedName("privilege")
        private int privilege;
        public int get_privilege() { return privilege; }
        public void set_privilege(int data) { this.privilege = data; }
    }
        
    public static class json_srv_identify {
        @SerializedName("encMinutiae")
        private String encMinutiae;
        public String get_encMinutiae() { return encMinutiae;}
        public void set_encMinutiae(String data) {this.encMinutiae = data;}

        @SerializedName("eSkey")
        private String eSkey;
        public String get_eSkey() { return eSkey; }
        public void set_eSkey(String data) { this.eSkey = data; }
        
        @SerializedName("iv")
        private String iv;
        public String get_iv() { return iv; }
        public void set_iv(String data_in) { this.iv = data_in; }
    }
        
        
    public static class json_srv_delete {
        @SerializedName("clientUserId")
        private String clientUserId;
        public String get_clientUserId() { return clientUserId;}
        public void set_clientUserId(String data) {this.clientUserId = data;}

        @SerializedName("deleteData")
        private String deleteData;
        public String get_deleteData() { return deleteData; }
        public void set_deleteData(String data) { this.deleteData = data; }
    }
}


