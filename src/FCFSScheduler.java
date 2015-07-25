import sun.awt.image.ImageWatched;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by test on 7/22/2015.
 */
public class FCFSScheduler implements Scheduler {
    private LinkedBlockingQueue<Process> readyQueue;
    private Queue<Process> ioQueue;
    private ExecutorService workerExecutor;
    private ExecutorCompletionService<Void> workerThreads;
    private ExecutorService ioExecutor;
    private ExecutorCompletionService<Process> ioThreads;
    private ExecutorService intermediateExecutor;
    private ExecutorCompletionService<Process> intermediateThreads;
    private final int NUMBER_OF_WORKER_THREADS;
    private final int NUMBER_OF_IO_THREADS;
    private final int NUMBER_OF_INTERMEDIATE_THREADS;
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
    }

    public boolean addToReadyQueue(Process process) {
        readyQueue.add(process);
    }

    public boolean addToIOQueue(Process process) {

    }

    public void run() {
        running = true;

        while (!workerExecutor.isShutdown()) {
            try {
                workerThreads.submit(readyQueue.take());
            } catch (InterruptedException e) { e.printStackTrace(); }


        }

    }

    public void stop() {
        workerExecutor.shutdown();
        ioExecutor.shutdown();
        intermediateExecutor.shutdown();
    }
}
