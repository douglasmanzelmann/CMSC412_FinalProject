import java.util.concurrent.Callable;

/**
 * Created by test on 7/22/2015.
 */
public class Process implements Callable<Process> {
    private static int count = 0;
    private int id;
    private long creationTime;
    private long startTime;
    private long endTime;
    private int serviceTime;
    private int memoryRequirement;

    private final int MIN_TIME = 50;
    private final int MAX_TIME = 50000;
    private final int TIME_RANGE = MAX_TIME - MIN_TIME;

    private final int MIN_MEMORY = 1;
    private final int MAX_MEMORY = 25;
    private final int MEMORY_RANGE = MAX_MEMORY - MIN_MEMORY;

    public Process() {
        id = ++count;
        creationTime = System.currentTimeMillis();
        serviceTime = (int)(Math.random() * TIME_RANGE) + MIN_TIME;
        memoryRequirement = (int)(Math.random() * MEMORY_RANGE) + MIN_MEMORY;
    }

    public Process call() {
        //System.out.println("Process" + id + " starting");
        startTime = System.currentTimeMillis();
        try {
            Thread.sleep(serviceTime);
        } catch (InterruptedException e) { e.printStackTrace(); }

        endTime = System.currentTimeMillis();
        //System.out.println("Process" + id + " done");
        return this;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getTotalTime() {
        return (endTime - startTime) + (startTime - creationTime);
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public int getMemoryRequirement() {
        return memoryRequirement;
    }

    public String toString() {
        return "Process ID: " + id + " Creation Time: " + creationTime + " Service Time: " + serviceTime;
    }
}
