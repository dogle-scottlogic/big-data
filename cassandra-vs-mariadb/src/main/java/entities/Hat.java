package entities;

import java.util.List;
import java.util.UUID;

/**
 * Created by dogle on 01/12/2016.
 */
public class Hat extends Product {

    private List<String>availableColours;
    private List<String> availableSizes;

    public Hat(
            UUID id,
            String name,
            List<String>availableColours,
            List<String> availableSizes,
            double weight,
            double price
            ) {
        super(id, name, weight, price);
        this.availableColours = availableColours;
        this.availableSizes = availableSizes;
    }

    public List<String> getAvailableColours() {
        return availableColours;
    }

    public void setAvailableColours(List<String> availableColours) {
        this.availableColours = availableColours;
    }

    public List<String> getAvailableSizes() {
        return availableSizes;
    }

    public void setAvailableSizes(List<String> availableSizes) {
        this.availableSizes = availableSizes;
    }
}
