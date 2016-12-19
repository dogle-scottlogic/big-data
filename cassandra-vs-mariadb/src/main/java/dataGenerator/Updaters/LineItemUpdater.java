package dataGenerator.Updaters;

import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.LineItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dogle on 02/12/2016.
 */
public class LineItemUpdater {

    private final Random random;

    public LineItemUpdater(Random random) {
        this.random = random;
    }

    public LineItem updateRandomLineItemField(LineItem originalLineItem) {
        ArrayList<Field> trimmedFields = getUpdatableFields(originalLineItem.getClass().getDeclaredFields());
        String fieldToUpdate = trimmedFields.get(this.random.nextInt(trimmedFields.size())).getName();
        if (fieldToUpdate.equals("quantity")) return updateLineItemQuantity(originalLineItem);
        return originalLineItem;
    }

    private LineItem updateLineItemQuantity(LineItem originalLineItem) {
        int newQuantity = random.nextInt(Settings.getIntSetting("MAX_PRODUCTS")) + 1;
        originalLineItem.setQuantity(newQuantity);
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
