package dataGenerator;

import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.Client;
import dataGenerator.enums.Enums;
import dataGenerator.transmission.Emitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.System.*;

/**
 * Created by dogle on 30/11/2016.
 */
public class Main {

    private static Thread thread = null;
    private static final int seed = Settings.getIntSetting("SEED");
    private static Random random = new Random(seed);
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        String input = "";
        dataGenerator.generators.EventGenerator eg = new dataGenerator.generators.EventGenerator(random);
        thread = new Thread(eg);

        out.println("Data Generator 1.0");
        while (!input.equals("exit")) {
            System.out.print(">");
            input = br.readLine();
            if (input.equals("run")) {
                Emitter.initialize();
                thread.start();
            }
            if (input.equals("stop")) {
                thread.interrupt();
                Emitter.end();
                thread = new Thread(eg);
            }
        }
    }
}
