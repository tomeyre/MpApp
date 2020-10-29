package tom.eyre.mpapp.util;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class GitUtil {

    public String getFile(String usernameInput, String passwordInput,String filePath){
        URL url;String username=usernameInput,password=passwordInput,file="";
        try {
            url = new URL("https://raw.githubusercontent.com/tomeyre/autobetter/master/READMET.md" + filePath);
            URLConnection uc;
            uc = url.openConnection();

            uc.setRequestProperty("X-Requested-With", "Curl");
            ArrayList<String> list=new ArrayList<String>();
            String userpass = username + ":" + password;
            String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));//needs Base64 encoder, apache.commons.codec
            uc.setRequestProperty("Authorization", basicAuth);

            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null)
                file=file+line+"\n";
            System.out.println(file);
            return file;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "fnf";
        } catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    public void storeFile(String json, String path){

    }
}
