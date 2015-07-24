import java.util.Queue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by test on 7/22/2015.
 */
public class FCFSScheduler implements Scheduler {
    private Queue<Process> readyQueue;
    private Queue<Process> ioQueue;
    private ExecutorCompletionService<Process> workerThreads;
    private ExecutorCompletionService<Process> ioThreads;

    public FCFSScheduler() {

    }

    public boolean addToReadyQueue(Process process) {
        readyQueue.add(process);
    }

    public boolean addToIOQueue(Process process) {

    }
}
