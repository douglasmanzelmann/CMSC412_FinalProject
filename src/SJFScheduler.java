import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by dmanzelmann on 7/24/2015.
 */
public class SJFScheduler extends Scheduler {
    private PriorityBlockingQueue<Process> readyQueue;
    private PriorityBlockingQueue<Process> ioQueue;
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

    public SJFScheduler() {
        NUMBER_OF_WORKER_THREADS = 6;
        NUMBER_OF_IO_THREADS = 1;
        NUMBER_OF_INTERMEDIATE_THREADS = 2;

        workerExecutor = Executors.newFixedThreadPool(NUMBER_OF_WORKER_THREADS);
        workerThreads = new ExecutorCompletionService<>(workerExecutor);

        ioExecutor = Executors.newSingleThreadExecutor();
        ioThreads = new ExecutorCompletionService<>(ioExecutor);

        intermediateExecutor = Executors.newFixedThreadPool(NUMBER_OF_INTERMEDIATE_THREADS);
        intermediateThreads = new ExecutorCompletionService<>(intermediateExecutor);

        readyQueue = new PriorityBlockingQueue<>(11, new ProcessServiceTimeComparator());
        ioQueue = new PriorityBlockingQueue<>(11, new ProcessServiceTimeComparator());

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
        int count = 0;
        while (!workerExecutor.isTerminated()) {
            if (count >= NUMBER_OF_WORKER_THREADS) {
                try {
                    workerThreads.take();
                    count--;
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
            if (readyQueue.peek() != null) {
                try {
                    System.out.println(readyQueue.peek());
                    System.out.println(readyQueue.size());
                    System.out.println("future list size: " + processedProcesses.size());
                    processedProcesses.add(workerThreads.submit(readyQueue.poll()));
                    count++;
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
