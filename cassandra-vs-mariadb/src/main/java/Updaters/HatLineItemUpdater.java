package Updaters;

import data_handlers.Settings;
import entities.products.Hat;
import entities.LineItem;
import generators.DataGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/**
 * Created by dogle on 02/12/2016.
 */
public class HatLineItemUpdater extends LineItemUpdater {
    private final Random random;
    private DataGenerator dataGenerator;

    public HatLineItemUpdater(Random random) {
        super(random);
        this.random = random;
        this.dataGenerator = new DataGenerator(this.random);
    }

    public LineItem updateRandomLineItemField(LineItem originalLineItem) {
        ArrayList<Field> trimmedFields = getUpdatableFields(originalLineItem.getClass().getDeclaredFields());
        String fieldToUpdate = trimmedFields.get(this.random.nextInt(trimmedFields.size())).getName();

        if (fieldToUpdate.equals("quantity")) return updateLineItemQuantity(originalLineItem);
        if (fieldToUpdate.equals("colour")) return updateLineItemColour(originalLineItem);
        if (fieldToUpdate.equals("size")) return updateLineItemSize(originalLineItem);
        return originalLineItem;
    }

    private LineItem updateLineItemQuantity(LineItem originalLineItem) {
        int newQuantity = random.nextInt(Settings.getIntSetting("MAX_PRODUCTS")) + 1;
        originalLineItem.setQuantity(newQuantity);
        return originalLineItem;
    }

    private LineItem updateLineItemColour(LineItem originalLineItem) {
        Hat hat = (Hat) originalLineItem.getProduct();
        String newColour = hat.getAvailableColours().get(this.random.nextInt(hat.getAvailableColours().size()));
        originalLineItem.setColor(newColour);
        return originalLineItem;
    }

    private LineItem updateLineItemSize(LineItem originalLineItem) {
        Hat hat = (Hat) originalLineItem.getProduct();
        String newSize = hat.getAvailableSizes().get(this.random.nextInt(hat.getAvailableSizes().size()));
        originalLineItem.setSize(newSize);
        return originalLineItem;
    }

    public ArrayList<Field> getUpdatableFields(Field[] declaredFields) {
        ArrayList<Field> trimmedFields = new ArrayList<Field>();
        for (Field field : declaredFields) {
            if (!field.getName().equals("product") && !field.getName().equals("id") && !field.getName().equals("linePrice")) {
                trimmedFields.add(field);
            }
        }
        return trimmedFields;
    }
}
