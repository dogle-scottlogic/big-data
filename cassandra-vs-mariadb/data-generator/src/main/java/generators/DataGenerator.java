package generators;

import data_handlers.JsonLoader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by dogle on 01/12/2016.
 */
public class DataGenerator {

    private JsonLoader jsonLoader;
    private Random random;

    public DataGenerator(Random random) {
        this.jsonLoader = new JsonLoader();
        this.random = random;
    }

    public String[] getNames(int num) {
        if(num == 0) return new String[]{};

        String[] names = new String[num];

        JSONObject client = this.jsonLoader.getJsonField("Client");
        JSONArray firstNames = (JSONArray)client.get("firstNames");
        JSONArray lastNames = (JSONArray) client.get("lastNames");
        for(int i = 0; i < names.length; i++) {
            names[i] = firstNames.get(random.nextInt(firstNames.size())) + " " + lastNames.get(random.nextInt(lastNames.size()));
        }
        return names;
    }

    public String[] getAddresses(int num) {
        if(num == 0) return new String[]{};

        String[] addresses = new String[num];

        JSONObject client = this.jsonLoader.getJsonField("Client");
        JSONArray streetNames = (JSONArray)client.get("streetName");
        JSONArray streetTypes = (JSONArray)client.get("streetType");
        JSONArray towns = (JSONArray)client.get("town");

        for(int i = 0; i < num; i++) {
            String address = Integer.toString(random.nextInt(998) + 1);
            address+= (", " + streetNames.get(random.nextInt(streetNames.size())));
            address+= (" " + streetTypes.get(random.nextInt(streetTypes.size())));
            address+= (", " + towns.get(random.nextInt(towns.size())));
            addresses[i] = address;
        }
        return  addresses;
    }

    public String getEmail(String name) {
        return name.replaceAll("\\s+","") + "@fakemail.com";
    }

    public HashMap<String, String> getHatData() {
        HashMap<String, String> hatData = new HashMap<String, String>();

        JSONObject hat = this.jsonLoader.getJsonField("Hat");
        JSONArray names = (JSONArray)hat.get("productNames");
        JSONArray colours = (JSONArray)hat.get("productColours");
        JSONArray sizes = (JSONArray)hat.get("productSizes");

        String name = (String)names.get(random.nextInt(names.size()));
        String colour = (String)colours.get(random.nextInt(colours.size()));
        String size = (String)sizes.get(random.nextInt(sizes.size()));

        hatData.put("name", name);
        hatData.put("colours", colour);
        hatData.put("sizes", size);
        return hatData;
    }

    public double generatePriceWeight(int min, int max) {
        double price = (double)this.random.nextInt(max) + min;
        if(price > max) price = max;
        double pennies = (this.random.nextInt(99) + 1)/(double)100;
        return  price + pennies;
    }
}
