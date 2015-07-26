import java.util.concurrent.Callable;

/**
 * Created by test on 7/22/2015.
 */
public class Process implements Callable<Process> {
    private static int count = 0;

    private int id;
    private int serviceTime;
    private int memoryRequirement;

    public Process() {
        id = ++count;
    }

    public Process call() {
        try {
            Thread.sleep(serviceTime);
        } catch (InterruptedException e) { e.printStackTrace(); }

        return this;
    }

    public String toString() {
        return "Process ID: " + id;
    }
}
