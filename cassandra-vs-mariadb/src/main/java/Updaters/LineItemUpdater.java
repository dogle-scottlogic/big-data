package Updaters;

import entities.LineItem;
import generators.DataGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dogle on 02/12/2016.
 */
public abstract class LineItemUpdater {

    private final Random random;
    private DataGenerator dataGenerator;

    public LineItemUpdater(Random random) {
        this.random = random;
        this.dataGenerator = new DataGenerator(this.random);
    }

    public abstract LineItem updateRandomLineItemField(LineItem originalLineItem);

    public abstract ArrayList<Field> getUpdatableFields(Field[] declaredFields);
}
