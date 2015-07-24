/**
 * Created by test on 7/22/2015.
 */
public class Process {
    private static int count = 0;

    private int id;
    private int serviceTime;
    private int memoryRequirement;

    public Process() {
        id = ++count;
    }

}
