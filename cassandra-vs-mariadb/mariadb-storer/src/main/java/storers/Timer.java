package storers;

/**
 * Created by dogle on 07/12/2016.
 */
public class Timer {

    private long startTime = 0;

    /**
     * Start the time
     * @return the time started
     */
    public long startTimer() {
        return startTime = System.nanoTime();
    }

    /**
     * Stop the timer
     * @return the time elapsed since timer was started
     */
    public long stopTimer() {
        long timeElapsed = System.nanoTime() - startTime;
        startTime = 0;
        return timeElapsed;
    }

}
