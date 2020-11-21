package tom.eyre.mpapp.util;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Ref;
import java.util.ArrayList;

public class GitUtil {

    public String getFile(String usernameInput, String passwordInput,String filePath){
        URL url;String username=usernameInput,password=passwordInput,file="";
        try {
            url = new URL("https://raw.githubusercontent.com/tomeyre/MpExpenses/main/" + filePath);
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
//        try {
//            Git git = Git.cloneRepository()
//                    .setURI("https://github.com/eclipse/jgit.git")
//                    .setDirectory(new File("res/xml/" + path))
//                    .call();
//
//            RevWalk walk = new RevWalk(git.getRepository());
//
//
//        } catch (GitAPIException e) {
//            e.printStackTrace();
//        }

    }
}
