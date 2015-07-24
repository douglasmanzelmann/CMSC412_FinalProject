import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by dmanzelmann on 7/24/2015.
 */
public class ProcessGenerator implements Callable<Process> {
    public Process call() {
        return new Process();
    }
}
