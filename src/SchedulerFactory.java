/**
 * Created by dmanzelmann on 7/24/2015.
 */
public class SchedulerFactory {
    public static Scheduler createScheduler(String type) {
        if (type.equals("FCFS"))
            return new FCFSScheduler();
        else if (type.equals("SJF"))
            return new SJFScheduler();

        return null;
    }
}
