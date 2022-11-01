package Hitomi.la;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hitomi_Download {
    private final String gallery_number;
    public Hitomi_Download(String gallery_number){
        this.gallery_number = gallery_number;
    }
    public void download_gallery(Path path) throws HitomiNotFoundException, TimeOutException{
        HashMap<Integer, String> gallery_hash = new get_gallery_info(this.gallery_number).get_hash();
        String Address = path.toString() + "/" + this.gallery_number;
        new File(Address).mkdirs();
        ExecutorService Threads = Executors.newFixedThreadPool(10);
        final int[] count = {0};

        for(int i = 1;i<=gallery_hash.size();i++){
            int num = i;
            String hash = gallery_hash.get(i);
            Runnable run = () -> {
                try {
                    download_image(hash, new File(Address + "/", num + ".webp"));
                    count[0]++;
                }catch (TooshorthashException e){
                    e.printStackTrace();
                }
            };
            Threads.submit(run);
        }
        Threads.shutdown();
        int TIME_COUNT = 0;
        int count_save = 0;
        try {
            while (!Threads.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                if (30000 <= TIME_COUNT) {
                    Threads.shutdownNow();
                    throw new TimeOutException();
                }
                TIME_COUNT++;
                if (count_save < count[0]) {
                    count_save = count[0];
                    System.out.println(count_save + "/" + gallery_hash.size() + " 페이지 출력 완료");
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void download_image(String hash, File file) throws TooshorthashException {
        while(true) {
            HttpURLConnection con;
            con = null;
            try {
                Matcher match = Pattern.compile("(..)(.)$").matcher(hash);
                int hash_to_num;
                if (match.find()) {
                    hash_to_num = Integer.parseInt(match.group(2) + match.group(1), 16);
                } else {
                    throw new TooshorthashException();
                }
                get_middle get = new get_middle();
                String Address = "https://" + get.get_subdomain(hash_to_num) + "a.hitomi.la/webp/" + get.middle_number +
                        "/" + hash_to_num + "/" + hash + ".webp";

                URL url = new URL(Address);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Referer", "https://hitomi.la/reader/" + this.gallery_number + "#1");

                InputStream input = con.getInputStream();
                FileOutputStream output = new FileOutputStream(file);

                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = input.read(buffer)) != -1)
                    output.write(buffer, 0, bytesRead);
                output.flush();
                output.getFD().sync();
                output.close();
                break;
            } catch (IOException e) {
                try {
                    assert con != null;
                    if (!(con.getResponseCode() == 503)) {
                        e.printStackTrace();
                        break;
                    }
                }catch (IOException i){
                    i.printStackTrace();
                }
            }
        }
    }
}
class get_middle{
    String middle_number;
    private String gg;
    get_middle(){
        try{
            URL url = new URL("https://ltn.hitomi.la/gg.js");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuilder full_js = new StringBuilder();
            while ((line = br.readLine()) != null)
                full_js.append(line).append('\n');
            this.gg = full_js.toString();
            this.middle_number = full_js.substring((full_js.length()-16), (full_js.length())-6);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    char get_subdomain(int hash_number) {
        try {
            String function = gg.substring(24, gg.length() - 115).replace("function(g) {", "function get(g){");
            ScriptEngineManager engine = new ScriptEngineManager();
            ScriptEngine javascript = engine.getEngineByName("JavaScript");
            javascript.eval(function);
            Invocable run = (Invocable) javascript;
            double Dnum = (Double)run.invokeFunction("get", hash_number);
            int num = (int) Dnum;

            if (num == 0)
                return 'a';
            else
                return 'b';
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

