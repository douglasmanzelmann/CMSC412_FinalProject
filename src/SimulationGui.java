import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.*;

/**
 * Created by douga_000 on 7/25/2015.
 */
public class SimulationGui {
    // Group all the controls
    private JPanel mainPanel = new JPanel();

    // SchedulerInterface Type Select
    private JPanel schedulerTypePanel = new JPanel();
    private JRadioButton fcfsRadioButton = new JRadioButton("FCFS");
    private JRadioButton sjfRadioButton = new JRadioButton("SJF");
    private ButtonGroup schedulerButtonGroup = new ButtonGroup();

    // Start / Stop Buttons
    private JPanel startStopPanel = new JPanel();
    private JButton startSimulationButton = new JButton();
    private JButton stopSimulationButton = new JButton();

    // Text area to display processes and stats
    private DefaultListModel<Process> processDefaultListModel = new DefaultListModel<>();
    private JList<Process> processJList = new JList<>(processDefaultListModel);
    private JScrollPane displayScrollPane = new JScrollPane(processJList);
    private JPanel contentPanel = new JPanel(new BorderLayout());

    private boolean systemRunning = false;

    public SimulationGui() {
        schedulerTypePanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(
                                EtchedBorder.RAISED, Color.GRAY, Color.DARK_GRAY),
                        "Select SchedulerInterface Algorithm"
                )
        );
        schedulerTypePanel.add(fcfsRadioButton);
        schedulerTypePanel.add(sjfRadioButton);

        startStopPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(
                                EtchedBorder.RAISED, Color.GRAY, Color.DARK_GRAY),
                        "Start/Stop the Simulation"
                )
        );
        startStopPanel.add(startSimulationButton);
        startStopPanel.add(stopSimulationButton);

        mainPanel.add(schedulerTypePanel);
        mainPanel.add(startStopPanel);


        contentPanel.add(mainPanel, BorderLayout.PAGE_START);
        contentPanel.add(displayScrollPane, BorderLayout.CENTER);

        startSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // need to do nothing or have a popup that
                // says neither scheduler algorithm is selected
                // or have one selected by default.

                startSimulation();
            }
        });
    }

    public JComponent getMainComponent() {
        return contentPanel;
    }

    public static void createAndShowGui() {
        SimulationGui simulationGui = new SimulationGui();

        JFrame frame = new JFrame("SchedulerInterface Simulation");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(simulationGui.getMainComponent());
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
            }
        });
    }

    public void startSimulation() {
        systemRunning = true;
        String selectedScheduler;
        if (fcfsRadioButton.isSelected())
            selectedScheduler = "FCFS";
        else
            selectedScheduler = "SJF";

        ProcessGeneratorWorker processGeneratorWorker =
                new ProcessGeneratorWorker(selectedScheduler);
        processGeneratorWorker.execute();

    }

    private class ProcessGeneratorWorker extends SwingWorker<Void, Process> {
        private SchedulerInterface scheduler;
        private ExecutorService schedulerThread;

        private ScheduledExecutorService scheduledExecutorService;
        private final int MIN_INTERVAL = 1;
        private final int MAX_INTERVAL = 5000;
        private final int RANGE = MAX_INTERVAL - MIN_INTERVAL;


        public ProcessGeneratorWorker(String schedulerType) {
            System.out.println(schedulerType);
            scheduler = SchedulerFactory.createScheduler(schedulerType);
            schedulerThread = Executors.newSingleThreadExecutor();
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        @Override
        protected Void doInBackground() throws Exception {
            schedulerThread.execute(scheduler);
            while (systemRunning) {
                int delay = (int)(Math.random() * RANGE) + MIN_INTERVAL;
                System.out.println("delay: " + delay);
                ScheduledFuture<Process> scheduledFuture =
                        scheduledExecutorService.schedule(
                                new ProcessGenerator(),
                                delay,
                                TimeUnit.MILLISECONDS);

                Process process = scheduledFuture.get();
                publish(process);

                //scheduler.addToReadyQueue(process);
            }

            scheduledExecutorService.shutdown();
            return null;
        }

        @Override
        protected void process(java.util.List<Process> processes) {
            System.out.println("in process");
            for (Process process : processes)
                processDefaultListModel.addElement(process);
        }

        @Override
        protected void done() {
            try {
                this.get();
            } catch (InterruptedException | ExecutionException e ) {
                e.printStackTrace();
            }
        }
    }
}
