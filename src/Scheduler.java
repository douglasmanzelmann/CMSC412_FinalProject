import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by douga_000 on 7/26/2015.
 */
public abstract class Scheduler implements SchedulerInterface, Callable<List<Future<Process>>> {

}
