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

    public LineItemUpdater(Random random) {
        this.random = random;
    }

    public abstract LineItem updateRandomLineItemField(LineItem originalLineItem);

    public abstract ArrayList<Field> getUpdatableFields(Field[] declaredFields);
}
