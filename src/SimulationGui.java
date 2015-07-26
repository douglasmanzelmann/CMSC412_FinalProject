import com.sun.org.glassfish.external.statistics.Stats;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
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
    private JButton startSimulationButton = new JButton("Start Simulation");
    private JButton stopSimulationButton = new JButton("Stop Simulation");

    // JList to display processes
    private DefaultListModel<Process> processDefaultListModel = new DefaultListModel<>();
    private JList<Process> processJList = new JList<>(processDefaultListModel);
    private JScrollPane displayScrollPane = new JScrollPane(processJList);


    // JList to display stats
    private DefaultListModel<String> statsDefaultListModel = new DefaultListModel<>();
    private JList<String> statsJList = new JList<>(statsDefaultListModel);
    private JScrollPane statsScrollPane = new JScrollPane(statsJList);

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
        contentPanel.add(statsScrollPane, BorderLayout.AFTER_LAST_LINE);

        startSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // need to do nothing or have a popup that
                // says neither scheduler algorithm is selected
                // or have one selected by default.

                startSimulation();
            }
        });

        stopSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSimulation();
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

    public void stopSimulation() {
        systemRunning = false;
    }

    private class ProcessGeneratorWorker extends SwingWorker<Void, Process> {
        private Scheduler scheduler;
        private ExecutorService schedulerThread;
        private StatsWorker statsWorker;

        private ScheduledExecutorService scheduledExecutorService;
        private final int MIN_INTERVAL = 1;
        private final int MAX_INTERVAL = 500;
        private final int RANGE = MAX_INTERVAL - MIN_INTERVAL;


        public ProcessGeneratorWorker(String schedulerType) {
            System.out.println(schedulerType);
            scheduler = SchedulerFactory.createScheduler(schedulerType);
            schedulerThread = Executors.newSingleThreadExecutor();
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        @Override
        protected Void doInBackground() throws Exception {
            Future<java.util.List<Future<Process>>> futureListFuture = schedulerThread.submit(scheduler);
            while (systemRunning) {
                int delay = (int)(Math.random() * RANGE) + MIN_INTERVAL;
                ScheduledFuture<Process> scheduledFuture =
                        scheduledExecutorService.schedule(
                                new ProcessGenerator(),
                                delay,
                                TimeUnit.MILLISECONDS);

                Process process = scheduledFuture.get();
                publish(process);

                scheduler.addToReadyQueue(process);
            }
            scheduler.stop();

            scheduledExecutorService.shutdown();
            schedulerThread.shutdown();

            statsWorker = new StatsWorker(futureListFuture);
            statsWorker.execute();

            return null;
        }

        @Override
        protected void process(java.util.List<Process> processes) {
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

    private class StatsWorker extends SwingWorker<Void, String> {
        Future<List<Future<Process>>> processedProcesses;
        SchedulerStats schedulerStats;

        public StatsWorker(Future<List<Future<Process>>> processedProcesses) {
            this.processedProcesses = processedProcesses;
            schedulerStats = new SchedulerStats();
        }

        @Override
        protected Void doInBackground() {
            try {
                List<Future<Process>> listOfFutureProcesses = processedProcesses.get();
                for (Future<Process> fp : listOfFutureProcesses) {
                    schedulerStats.addProcess(fp.get());
                }
            } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }

            publish("Total Number of Processes: " + schedulerStats.getNumberOfProcesses());
            publish("Minimum Total Time: " + schedulerStats.getMinimumTotalTime());
            publish("Maximum Total Time: " + schedulerStats.getMaximumTotalTime());
            publish("Average Total Time: " + schedulerStats.getAverageTotalTime());

            publish("Average Wait Time: " + schedulerStats.getAverageWaitTime());
            publish("Average Turn Around Time: " + schedulerStats.getAverageTurnAroundTime());
            return null;
        }

        @Override
        protected void process(List<String> stats) {
            for (String stat : stats) {
                statsDefaultListModel.addElement(stat);
            }
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
