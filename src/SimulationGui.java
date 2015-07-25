import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Created by douga_000 on 7/25/2015.
 */
public class SimulationGui {
    // Group all the controls
    private JPanel mainPanel = new JPanel();

    // Scheduler Type Select
    private JPanel schedulerTypePanel = new JPanel();
    private JRadioButton fcfsRadioButton = new JRadioButton("FCFS");
    private JRadioButton sjfRadioButton = new JRadioButton("SJF");
    private ButtonGroup schedulerButtonGroup = new ButtonGroup();

    // Start / Stop Buttons
    private JPanel startStopPanel = new JPanel();
    private JButton startSimulationButton = new JButton();
    private JButton stopSimulationButton = new JButton();

    // Text area to display processes and stats
    private JScrollPane displayScrollPane = new JScrollPane();
    private DefaultListModel<Process> processDefaultListModel = new DefaultListModel<>();
    private JList<Process> processJList = new JList<>(processDefaultListModel);

    private JPanel contentPanel = new JPanel(new BorderLayout());

    public SimulationGui() {
        schedulerTypePanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(
                                EtchedBorder.RAISED, Color.GRAY, Color.DARK_GRAY),
                        "Select Scheduler Algorithm"
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
    }

    public JComponent getMainComponent() {
        return contentPanel;
    }

    public static void createAndShowGui() {
        SimulationGui simulationGui = new SimulationGui();

        JFrame frame = new JFrame("Scheduler Simulation");
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

}
