/**
 * Created by test on 7/22/2015.
 */
public interface Scheduler {
    boolean addToReadyQueue(Process process);
    boolean addToIOQueue(Process process);
}
