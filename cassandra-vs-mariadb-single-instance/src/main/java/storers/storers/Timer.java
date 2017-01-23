package storers.storers;

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
        this.startTime = System.nanoTime();
        return this.startTime;
    }

    /**
     * Stop the timer
     * @return the time elapsed since timer was started
     */
    public long stopTimer() {
        long timeElapsed = System.nanoTime() - this.startTime;
        this.startTime = 0;
        return timeElapsed;
    }

}
