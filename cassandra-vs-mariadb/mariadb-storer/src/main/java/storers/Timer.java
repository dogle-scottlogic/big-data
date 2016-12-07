package storers;

/**
 * Created by dogle on 07/12/2016.
 */
public class Timer {

    private static long startTime = 0;

    /**
     * Start the time
     * @return the time started
     */
    public static long startTimer() {
        return startTime = System.nanoTime();
    }

    /**
     * Stop the timer
     * @return the time elapsed since timer was started
     */
    public static long stopTimer() {
        long timeElapsed = System.nanoTime() - startTime;
        startTime = 0;
        return timeElapsed;
    }

}
