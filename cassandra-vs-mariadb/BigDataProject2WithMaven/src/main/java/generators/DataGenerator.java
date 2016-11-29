/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import redis.clients.jedis.Jedis;

/**
 *
 * @author dogle
 */
public class DataGenerator {

    private String filePath = "./data.txt";

    public List generateNames() {
        List names = null;
        return names;
    }

    public boolean populateRedis() {
        Jedis jedis = new Jedis("localhost");
        String data = fileReader("#names");
        String[] n = data.split(",");
        for(int i = 0; i < n.length; i++) {
            jedis.lpush("names", n[i]);
        }
//        List<String> a = jedis.lrange("names", 0, -1);
        return true;
    }

    private String fileReader(String start) {
        String text = "";
        String path = new File(filePath).getAbsolutePath();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            boolean reading = false;
            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                if (reading && sCurrentLine.trim().charAt(0) == '#') {
                    return text;
                }
                if (sCurrentLine.trim().equals(start)) {
                    reading = true;
                } else if (reading) {
                    text = text + sCurrentLine.trim();
                }
            }
        } catch (IOException e) {
            System.out.println(e.fillInStackTrace());
            return text;
        }
        return text;
    }

}
