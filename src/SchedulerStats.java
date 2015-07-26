import java.util.ArrayList;
import java.util.List;

/**
 * Created by douga_000 on 7/26/2015.
 */
public class SchedulerStats {
    private List<Process> listOfProcess;
    private int numberOfProcesses;
    private long minimumTotalTime = Long.MAX_VALUE;
    private long maximumTotalTime = Long.MIN_VALUE;
    private long totalTime;

    private long totalWaitTime;
    private long totalTurnAroundTime;

    public SchedulerStats() {
        totalTime = 0;
        numberOfProcesses = 0;
        listOfProcess = new ArrayList<>();

        totalWaitTime = 0;
        totalTurnAroundTime = 0;
    }

    public void addProcess(Process process) {
        numberOfProcesses++;
        long processTotalTime = process.getTotalTime();
        totalTime += processTotalTime;

        if (processTotalTime < minimumTotalTime)
            minimumTotalTime = processTotalTime;
        else if (processTotalTime > maximumTotalTime)
            maximumTotalTime = processTotalTime;

        totalWaitTime += (process.getStartTime() - process.getCreationTime());
        totalTurnAroundTime += (process.getEndTime() - process.getCreationTime());
    }

    public int getNumberOfProcesses() {
        return numberOfProcesses;
    }

    public long getMinimumTotalTime() {
        return minimumTotalTime;
    }

    public long getMaximumTotalTime() {
        return maximumTotalTime;
    }

    public long getAverageTotalTime() {
        return totalTime / numberOfProcesses;
    }

    public double getAverageWaitTime() {
        return (double)totalWaitTime / numberOfProcesses;
    }

    public double getAverageTurnAroundTime() {
        return (double)totalTurnAroundTime / numberOfProcesses;
    }
}
