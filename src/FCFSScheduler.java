import java.util.Queue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by test on 7/22/2015.
 */
public class FCFSScheduler implements Scheduler {
    private Queue<Process> readyQueue;
    private Queue<Process> ioQueue;
    private ScheduledThreadPoolExecutor workerThreads;
    private ScheduledThreadPoolExecutor ioThreads;
    private ScheduledThreadPoolExecutor intermediateThreads;
    private final int NUMBER_OF_WORKER_THREADS;
    private final int NUMBER_OF_IO_THREADS;
    private final int NUMBER_OF_INTERMEDIATE_THREADS;

    public FCFSScheduler() {
        NUMBER_OF_WORKER_THREADS = 6;
        NUMBER_OF_IO_THREADS = 1;
        NUMBER_OF_INTERMEDIATE_THREADS = 2;

        workerThreads = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKER_THREADS);
        ioThreads = new ScheduledThreadPoolExecutor(NUMBER_OF_IO_THREADS);
        intermediateThreads = new ScheduledThreadPoolExecutor(NUMBER_OF_INTERMEDIATE_THREADS);
    }

    public boolean addToReadyQueue(Process process) {
        readyQueue.add(process);
    }

    public boolean addToIOQueue(Process process) {

    }
}
