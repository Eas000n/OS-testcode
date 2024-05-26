import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Job {
    String name;
    int arrivalTime;
    int burstTime;
    int priority;
    int startTime;
    int finishTime;
    int remainingTime;
    int turnaroundTime;
    double weightedTurnaroundTime;

    public Job(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }
}

public class SchedulerSimulation extends JFrame {
    private JTextField nameField, arrivalField, burstField, priorityField, timesliceField;
    private JComboBox<String> algorithmComboBox;
    private JTable jobTable, resultTable;
    private DefaultTableModel jobTableModel, resultTableModel;
    private JButton addButton, clearButton, runButton, resetButton;
    private Timer timer;
    private List<Job> jobs = new ArrayList<>();
    private List<Job> readyQueue = new ArrayList<>();
    private Job currentJob;
    private int currentTime;
    private int timeSlice;
    private boolean isRunning = false;

    public SchedulerSimulation() {
        setTitle("作业调度模拟程序");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 初始化组件
        nameField = new JTextField(5);
        arrivalField = new JTextField(5);
        burstField = new JTextField(5);
        priorityField = new JTextField(5);
        timesliceField = new JTextField(5);
        algorithmComboBox = new JComboBox<>(new String[]{"FCFS", "SJF", "HRN", "RR", "Static Priority"});
        addButton = new JButton("添加进程");
        clearButton = new JButton("清空输入");
        runButton = new JButton("运行调度");
        resetButton = new JButton("重置队列");

        jobTableModel = new DefaultTableModel(new Object[]{"进程名", "到达时间", "服务时间", "优先级"}, 0);
        jobTable = new JTable(jobTableModel);
        resultTableModel = new DefaultTableModel(new Object[]{"进程名", "开始时间", "完成时间", "周转时间", "带权周转时间"}, 0);
        resultTable = new JTable(resultTableModel);

        // 布局设置
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        JPanel jobInputPanel = new JPanel();
        jobInputPanel.add(new JLabel("进程名:"));
        jobInputPanel.add(nameField);
        jobInputPanel.add(new JLabel("到达时间:"));
        jobInputPanel.add(arrivalField);
        jobInputPanel.add(new JLabel("服务时间:"));
        jobInputPanel.add(burstField);
        jobInputPanel.add(new JLabel("优先级:"));
        jobInputPanel.add(priorityField);
        jobInputPanel.add(addButton);
        jobInputPanel.add(clearButton);

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("时间片:"));
        controlPanel.add(timesliceField);
        controlPanel.add(new JLabel("调度算法:"));
        controlPanel.add(algorithmComboBox);
        controlPanel.add(runButton);
        controlPanel.add(resetButton);

        inputPanel.add(jobInputPanel);
        inputPanel.add(controlPanel);

        JPanel tablePanel = new JPanel(new GridLayout(2, 1));
        tablePanel.add(new JScrollPane(jobTable));
        tablePanel.add(new JScrollPane(resultTable));

        add(inputPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        // 事件监听器
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addJob();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearInputs();
            }
        });

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startScheduling();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetQueues();
            }
        });

        // 初始化计时器
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimer();
            }
        });

        // 输入变化检测
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkInputs();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkInputs();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkInputs();
            }
        });

        arrivalField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkInputs();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkInputs();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkInputs();
            }
        });

        burstField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkInputs();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkInputs();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkInputs();
            }
        });

        priorityField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkInputs();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkInputs();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkInputs();
            }
        });

        checkInputs();
    }

    private void addJob() {
        String name = nameField.getText();
        int arrivalTime = Integer.parseInt(arrivalField.getText());
        int burstTime = Integer.parseInt(burstField.getText());
        int priority = Integer.parseInt(priorityField.getText());

        Job job = new Job(name, arrivalTime, burstTime, priority);
        jobs.add(job);
        jobTableModel.addRow(new Object[]{name, arrivalTime, burstTime, priority});
        clearInputs();
    }

    private void clearInputs() {
        nameField.setText("");
        arrivalField.setText("");
        burstField.setText("");
        priorityField.setText("");
    }

    private void startScheduling() {
        if (isRunning) return;

        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        if (selectedAlgorithm.equals("RR")) {
            timeSlice = Integer.parseInt(timesliceField.getText());
        }

        currentTime = 0;
        readyQueue.clear();
        readyQueue.addAll(jobs);
        isRunning = true;
        timer.start();
    }

    private void updateTimer() {
        currentTime++;
        if (currentJob != null) {
            currentJob.remainingTime--;
            if (currentJob.remainingTime == 0) {
                currentJob.finishTime = currentTime;
                currentJob.turnaroundTime = currentJob.finishTime - currentJob.arrivalTime;
                currentJob.weightedTurnaroundTime = (double) currentJob.turnaroundTime / currentJob.burstTime;
                resultTableModel.addRow(new Object[]{currentJob.name, currentJob.startTime, currentJob.finishTime, currentJob.turnaroundTime, currentJob.weightedTurnaroundTime});
                readyQueue.remove(currentJob);
                currentJob = null;
            }
        }
        if (currentJob == null && !readyQueue.isEmpty()) {
            selectNextJob();
        }
        if (readyQueue.isEmpty() && currentJob == null) {
            timer.stop();
            isRunning = false;
        }
    }

    private void selectNextJob() {
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        switch (selectedAlgorithm) {
            case "FCFS":
                readyQueue.sort(Comparator.comparingInt(p -> p.arrivalTime));
                break;
            case "SJF":
                readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));
                break;
            case "HRN":
                for (Job job : readyQueue) {
                    job.weightedTurnaroundTime = 1 + (double) (currentTime - job.arrivalTime) / job.burstTime;
                }
                readyQueue.sort((p1, p2) -> Double.compare(p2.weightedTurnaroundTime, p1.weightedTurnaroundTime));
                break;
            case "Static Priority":
                readyQueue.sort(Comparator.comparingInt(p -> p.priority));
                break;
            case "RR":
                // RR does not need to sort the ready queue
                break;
        }
        if (!readyQueue.isEmpty()) {
            currentJob = readyQueue.get(0);
            currentJob.startTime = currentTime;
            if (selectedAlgorithm.equals("RR")) {
                readyQueue.remove(0);
                readyQueue.add(currentJob);
            }
        }
    }

    private void resetQueues() {
        jobs.clear();
        readyQueue.clear();
        currentJob = null;
        currentTime = 0;
        isRunning = false;
        timer.stop();
        jobTableModel.setRowCount(0);
        resultTableModel.setRowCount(0);
        algorithmComboBox.setSelectedIndex(0);
        timesliceField.setText("");
    }

    private void checkInputs() {
        boolean enableAddButton = !nameField.getText().trim().isEmpty()
                && !arrivalField.getText().trim().isEmpty()
                && !burstField.getText().trim().isEmpty()
                && !priorityField.getText().trim().isEmpty();
        addButton.setEnabled(enableAddButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SchedulerSimulation().setVisible(true);
            }
        });
    }
}
