import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by dmanzelmann on 7/24/2015.
 */
public class SchedulerSimulation {
    private Scheduler scheduler;
    private ProcessGenerator processGenerator;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final int MIN_INTERVAL = 1; // 1 millisecond
    private final int MAX_INTERVAL = 10000; // 10 seconds
    private final int RANGE = MAX_INTERVAL - MIN_INTERVAL;

    private boolean systemRunning;

    public SchedulerSimulation() {
        processGenerator = new ProcessGenerator();
    }

    public void setScheduler(String type) {
        scheduler = SchedulerFactory.createScheduler(type);
    }

    public void run() {
        systemRunning = true;
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

        while (systemRunning) {
            long delay = (int)(Math.random() * RANGE) + MIN_INTERVAL;
            Future<Process> processFuture = scheduledThreadPoolExecutor.schedule(new ProcessGenerator(), delay, TimeUnit.MILLISECONDS);
            try {
                scheduler.addToReadyQueue(processFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        systemRunning = false;
    }
}
