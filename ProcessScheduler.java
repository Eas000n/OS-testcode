import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

class Process {
    String name;
    int arrivalTime;
    int burstTime;
    int priority;
    int remainingTime;
    int startTime;
    int endTime;

    public Process(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }
}

public class ProcessScheduler extends JFrame {
    private List<Process> processes;
    private DefaultTableModel readyQueueModel;
    private DefaultTableModel resultTableModel;
    private JTextArea outputArea;
    private JTextField nameField;
    private JTextField arrivalTimeField;
    private JTextField burstTimeField;
    private JComboBox<String> priorityComboBox;
    private JComboBox<String> algorithmComboBox;
    private JButton addButton;
    private JButton clearButton;
    private JButton simulateButton;
    private int currentTime;

    public ProcessScheduler() {
        processes = new ArrayList<>();
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        nameField = new JTextField(10);
        arrivalTimeField = new JTextField(5);
        burstTimeField = new JTextField(5);
        priorityComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        algorithmComboBox = new JComboBox<>(new String[]{"FCFS", "SJF", "RR"});

        addButton = new JButton("Add Process");
        addButton.addActionListener(this::addProcess);
        addButton.setEnabled(false);

        clearButton = new JButton("Clear Inputs");
        clearButton.addActionListener(e -> clearInputs());

        simulateButton = new JButton("Simulate");
        simulateButton.addActionListener(e -> simulateAlgorithm());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Arrival Time:"));
        inputPanel.add(arrivalTimeField);
        inputPanel.add(new JLabel("Burst Time:"));
        inputPanel.add(burstTimeField);
        inputPanel.add(new JLabel("Priority:"));
        inputPanel.add(priorityComboBox);
        inputPanel.add(new JLabel("Algorithm:"));
        inputPanel.add(algorithmComboBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(simulateButton);

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(inputPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        readyQueueModel = new DefaultTableModel(new String[]{"Name", "Arrival Time", "Burst Time", "Priority", "Remaining Time"}, 0);
        JTable readyQueueTable = new JTable(readyQueueModel);
        JScrollPane readyQueueScrollPane = new JScrollPane(readyQueueTable);

        resultTableModel = new DefaultTableModel(new String[]{"Name", "Start Time", "End Time", "Turnaround Time", "Weighted Turnaround Time"}, 0);
        JTable resultTable = new JTable(resultTableModel);
        JScrollPane resultTableScrollPane = new JScrollPane(resultTable);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1));
        tablesPanel.add(new JLabel("Ready Queue"));
        tablesPanel.add(readyQueueScrollPane);
        tablesPanel.add(new JLabel("Result Table"));
        tablesPanel.add(resultTableScrollPane);

        add(controlPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        nameField.getDocument().addDocumentListener(new InputChangeListener());
        arrivalTimeField.getDocument().addDocumentListener(new InputChangeListener());
        burstTimeField.getDocument().addDocumentListener(new InputChangeListener());
        priorityComboBox.addActionListener(e -> checkInputs());
        algorithmComboBox.addActionListener(e -> checkInputs());

        setTitle("Process Scheduler");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void addProcess(ActionEvent e) {
        String name = nameField.getText();
        int arrivalTime = Integer.parseInt(arrivalTimeField.getText());
        int burstTime = Integer.parseInt(burstTimeField.getText());
        int priority = Integer.parseInt((String) priorityComboBox.getSelectedItem());

        Process process = new Process(name, arrivalTime, burstTime, priority);
        processes.add(process);

        readyQueueModel.addRow(new Object[]{name, arrivalTime, burstTime, priority, burstTime});

        clearInputs();
        checkInputs();
    }

    private void clearInputs() {
        nameField.setText("");
        arrivalTimeField.setText("");
        burstTimeField.setText("");
        priorityComboBox.setSelectedIndex(0);
    }

    private void simulateAlgorithm() {
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        if (selectedAlgorithm.equals("FCFS")) {
            simulateFCFS();
        } else if (selectedAlgorithm.equals("SJF")) {
            simulateSJF();
        } else if (selectedAlgorithm.equals("RR")) {
            simulateRR(2);
        }
    }

    private void simulateFCFS() {
        outputArea.setText("Simulating FCFS...\n");
        List<Process> fcfsProcesses = new ArrayList<>(processes);
        fcfsProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));
        simulate(fcfsProcesses);
    }

    private void simulateSJF() {
        outputArea.setText("Simulating SJF...\n");
        List<Process> sjfProcesses = new ArrayList<>(processes);
        sjfProcesses.sort(Comparator.comparingInt(p -> p.burstTime));
        simulate(sjfProcesses);
    }

    private void simulateRR(int quantum) {
        outputArea.setText("Simulating RR...\n");
        List<Process> rrProcesses = new ArrayList<>(processes);
        Queue<Process> queue = new LinkedList<>(rrProcesses);
        currentTime = 0;

        while (!queue.isEmpty()) {
            Process current = queue.poll();
            if (current.remainingTime > quantum) {
                if (currentTime < current.arrivalTime) {
                    currentTime = current.arrivalTime;
                }
                current.startTime = currentTime;
                currentTime += quantum;
                current.remainingTime -= quantum;
                queue.offer(current);
            } else {
                if (currentTime < current.arrivalTime) {
                    currentTime = current.arrivalTime;
                }
                current.startTime = currentTime;
                currentTime += current.remainingTime;
                current.remainingTime = 0;
                current.endTime = currentTime;
                outputArea.append("Process " + current.name + " finished at time " + current.endTime + "\n");
            }
            updateReadyQueue();
        }
        calculateAndDisplayStatistics();
    }

    private void simulate(List<Process> processList) {
        currentTime = 0;

        for (Process process : processList) {
            if (currentTime < process.arrivalTime) {
                currentTime = process.arrivalTime;
            }
            process.startTime = currentTime;
            currentTime += process.burstTime;
            process.endTime = currentTime;
            outputArea.append("Process " + process.name + " finished at time " + process.endTime + "\n");
            updateReadyQueue();
        }
        calculateAndDisplayStatistics();
    }

    private void updateReadyQueue() {
        readyQueueModel.setRowCount(0);
        for (Process process : processes) {
            readyQueueModel.addRow(new Object[]{process.name, process.arrivalTime, process.burstTime, process.priority, process.remainingTime});
        }
    }

    private void calculateAndDisplayStatistics() {
        double totalTurnaroundTime = 0;
        double totalWeightedTurnaroundTime = 0;

        resultTableModel.setRowCount(0);

        for (Process process : processes) {
            int turnaroundTime = process.endTime - process.arrivalTime;
            totalTurnaroundTime += turnaroundTime;
            double weightedTurnaroundTime = (double) turnaroundTime / process.burstTime;
            totalWeightedTurnaroundTime += weightedTurnaroundTime;
            resultTableModel.addRow(new Object[]{process.name, process.startTime, process.endTime, turnaroundTime, weightedTurnaroundTime});
        }

        double avgTurnaroundTime = totalTurnaroundTime / processes.size();
        double avgWeightedTurnaroundTime = totalWeightedTurnaroundTime / processes.size();

        outputArea.append("Average Turnaround Time: " + avgTurnaroundTime + "\n");
        outputArea.append("Average Weighted Turnaround Time: " + avgWeightedTurnaroundTime + "\n");
    }

    private void checkInputs() {
        addButton.setEnabled(!nameField.getText().trim().isEmpty() && !arrivalTimeField.getText().trim().isEmpty() && !burstTimeField.getText().trim().isEmpty() && priorityComboBox.getSelectedIndex() != -1);
    }

    private class InputChangeListener implements javax.swing.event.DocumentListener {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { checkInputs(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { checkInputs(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { checkInputs(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProcessScheduler::new);
    }
}
