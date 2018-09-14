/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo_java2webapi;
import java.net.HttpURLConnection;
import java.io.*;

/**
 *
 * @author lucky
 */
public class Demo_Java2WebAPI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        float y = 0;
        System.out.print("Hello, world\n");
        PostJson2WebAPI(false, "/api/load_fp_srv", "");

        
    }
    
    private static String PostJson2WebAPI(boolean  https_en, String route ,String json_string)
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
        
        HttpURLConnection connection = null; 
        
        try
        {
            java.net.URL url = new java.net.URL(protocol + route);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(json_string.length())); //?
            connection.setRequestProperty("User-agent","myapp");
            connection.setUseCaches(false); 
            connection.setDoOutput(true);
            connection.setDoInput(true);
            
            //Write out
            DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
            wr.writeBytes (json_string);
            wr.flush ();
            wr.close ();
            
            
            //Get Response	
            InputStream is = connection.getInputStream();
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
            if(connection != null) 
            {
                connection.disconnect(); 
                
            }
        }    
        return ret_str;
    }
    
}
