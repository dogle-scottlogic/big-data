package storers.storers;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by dogle on 15/12/2016.
 */
public class Order {

    private final static Logger LOG = Logger.getLogger(Order.class);
    private String orderId;
    private ArrayList<HashMap<String, String>> lineItems = new ArrayList<>();
    private String clientId;
    private String status;
    private Double subTotal;
    private String date;

    public Order(String data) {
        this.orderId = data;
    }

    public Order(JSONObject data) {
        this.orderId = (String) data.get("id");
        try {
            Long dateLong = (Long) data.get("date");
            this.subTotal = (Double) data.get("subTotal");
            this.clientId = (String) ((JSONObject) data.get("client")).get("id");
            this.date = new Date(dateLong).toString();
            this.status = (String) data.get("status");
            JSONArray lineItems = (JSONArray) data.get("lineItems");
            for (Object lineItem1 : lineItems) {
                // Extract values
                HashMap<String, String> lineItemMap = new HashMap<>();
                JSONObject lineItem = (JSONObject) lineItem1;
                lineItemMap.put("id", (String) lineItem.get("id"));
                lineItemMap.put("productId", (String) ((JSONObject) lineItem.get("product")).get("id"));
                lineItemMap.put("quantity", String.valueOf(lineItem.get("quantity")));
                lineItemMap.put("linePrice", String.valueOf(lineItem.get("linePrice")));
                this.lineItems.add(lineItemMap);
            }
        } catch (RuntimeException e) {
            LOG.warn("Failed to create order", e);
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ArrayList<HashMap<String, String>> getLineItems() {
        return lineItems;
    }

    public void setLineItems(ArrayList<HashMap<String, String>> lineItems) {
        this.lineItems = lineItems;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
