package Hitomi.la;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class get_gallery_info {
    final String base_Address = "https://ltn.hitomi.la/galleries/";
    String Address;
    JSONObject gallery_info;
    public get_gallery_info(String gallery_number) throws HitomiNotFoundException{
        try {
            this.Address = this.base_Address + gallery_number + ".js";
            URL url = new URL(this.Address);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            String line;
            StringBuilder full_code = new StringBuilder();
            BufferedReader br;
            try {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }catch (FileNotFoundException e){
                throw new HitomiNotFoundException();
            }
            while ((line = br.readLine()) != null)
                full_code.append(line);

            this.gallery_info = new JSONObject(full_code.substring(18));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public HashMap<Integer, String> get_hash(){
        JSONArray images = this.gallery_info.getJSONArray("files");
        int length = images.length();
        HashMap<Integer, String> result = new HashMap<>();

        for(int i = 1;i<=length;i++)
            result.put(i, images.getJSONObject(i-1).getString("hash"));

        return result;
    }
}

