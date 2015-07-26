import java.util.concurrent.Callable;

/**
 * Created by test on 7/22/2015.
 */
public interface SchedulerInterface {
    boolean addToReadyQueue(Process process);
    boolean addToIOQueue(Process process);
}
