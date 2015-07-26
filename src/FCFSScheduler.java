import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by test on 7/22/2015.
 */
public class FCFSScheduler extends Scheduler {
    private Queue<Process> readyQueue;
    private Queue<Process> ioQueue;
    private ExecutorService workerExecutor;
    private ExecutorCompletionService<Process> workerThreads;
    private ExecutorService ioExecutor;
    private ExecutorCompletionService<Process> ioThreads;
    private ExecutorService intermediateExecutor;
    private ExecutorCompletionService<Process> intermediateThreads;

    private final int NUMBER_OF_WORKER_THREADS;
    private final int NUMBER_OF_IO_THREADS;
    private final int NUMBER_OF_INTERMEDIATE_THREADS;

    private List<Future<Process>> processedProcesses;

    private boolean running;


    public FCFSScheduler() {
        NUMBER_OF_WORKER_THREADS = 6;
        NUMBER_OF_IO_THREADS = 1;
        NUMBER_OF_INTERMEDIATE_THREADS = 2;

        workerExecutor = Executors.newFixedThreadPool(NUMBER_OF_WORKER_THREADS);
        workerThreads = new ExecutorCompletionService<>(workerExecutor);

        ioExecutor = Executors.newSingleThreadExecutor();
        ioThreads = new ExecutorCompletionService<>(ioExecutor);

        intermediateExecutor = Executors.newFixedThreadPool(NUMBER_OF_INTERMEDIATE_THREADS);
        intermediateThreads = new ExecutorCompletionService<>(intermediateExecutor);

        readyQueue = new LinkedBlockingQueue<>();
        ioQueue = new LinkedBlockingQueue<>();

        processedProcesses = new ArrayList<>();
    }

    public boolean addToReadyQueue(Process process) {
        readyQueue.add(process);
        return true;
    }

    public boolean addToIOQueue(Process process) {

        return true;
    }

    public void stop() {
        workerExecutor.shutdown();
        ioExecutor.shutdown();
        intermediateExecutor.shutdown();
    }

    public List<Future<Process>> call() {
        while (!workerExecutor.isShutdown()) {
            if (readyQueue.peek() != null) {
                try {
                    processedProcesses.add(workerThreads.submit(readyQueue.poll()));
                } catch (RejectedExecutionException e ) {
                    if (!workerExecutor.isShutdown()) { e.printStackTrace(); }
                }
            }

            if (ioQueue.peek() != null) {
                processedProcesses.add(ioThreads.submit(ioQueue.poll()));
            }
        }

        return processedProcesses;
    }
}
