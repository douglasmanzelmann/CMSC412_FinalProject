import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by douga_000 on 7/26/2015.
 */
public class ProcessServiceTimeComparator implements Comparator<Process> {
    @Override
    public int compare(Process a, Process b) {
        int processAServiceTime = a.getServiceTime();
        int processBServiceTime = b.getServiceTime();

        if (processAServiceTime < processBServiceTime)
            return -1;
        else if (processAServiceTime > processBServiceTime)
            return 1;

        return 0; //equal
    }

    public static void main(String[] args) {
        Process a = new Process();
        Process b = new Process();
        Process c = new Process();

        PriorityBlockingQueue<Process> queue = new PriorityBlockingQueue<>(11, new ProcessServiceTimeComparator());
        queue.add(a);
        queue.add(b);
        queue.add(c);

        while (queue.peek() != null) {
            System.out.println(queue.poll());
        }
    }
}
