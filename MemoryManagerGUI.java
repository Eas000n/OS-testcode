package com.example.memorymanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemoryManagerGUI extends JFrame {
    private JTable memoryTable;
    private JTable jobTable;
    private JButton generateMemoryButton;
    private JButton generateJobsButton;
    private JButton allocateButton;
    private JButton resetButton;
    private JButton allocateRemainingButton;
    private JButton clearAllButton;
    private JRadioButton firstFitRadio;
    private JRadioButton nextFitRadio;
    private JRadioButton bestFitRadio;
    private JRadioButton worstFitRadio;
    private ButtonGroup algorithmGroup;
    private MemoryManager manager;
    private MemoryPanel memoryPanel;

    public MemoryManagerGUI() {
        setTitle("Memory Manager Simulation");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create and set up the memory and job tables
        memoryTable = new JTable(new String[0][3], new String[]{"Start", "Size", "Free"});
        jobTable = new JTable(new String[0][2], new String[]{"ID", "Size"});

        // Create buttons and radio buttons
        generateMemoryButton = new JButton("Generate Memory Blocks");
        generateJobsButton = new JButton("Generate Jobs");
        allocateButton = new JButton("Allocate");
        resetButton = new JButton("Reset");
        allocateRemainingButton = new JButton("Allocate Remaining");
        clearAllButton = new JButton("Clear All");

        firstFitRadio = new JRadioButton("First Fit");
        nextFitRadio = new JRadioButton("Next Fit");
        bestFitRadio = new JRadioButton("Best Fit");
        worstFitRadio = new JRadioButton("Worst Fit");

        algorithmGroup = new ButtonGroup();
        algorithmGroup.add(firstFitRadio);
        algorithmGroup.add(nextFitRadio);
        algorithmGroup.add(bestFitRadio);
        algorithmGroup.add(worstFitRadio);

        // Disable buttons initially
        allocateButton.setEnabled(false);
        resetButton.setEnabled(false);
        allocateRemainingButton.setEnabled(false);
        clearAllButton.setEnabled(false);

        // Set up the panel with buttons and radio buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 3));
        controlPanel.add(generateMemoryButton);
        controlPanel.add(generateJobsButton);
        controlPanel.add(allocateButton);
        controlPanel.add(resetButton);
        controlPanel.add(allocateRemainingButton);
        controlPanel.add(clearAllButton);
        controlPanel.add(firstFitRadio);
        controlPanel.add(nextFitRadio);
        controlPanel.add(bestFitRadio);
        controlPanel.add(worstFitRadio);

        // Add table and control panel to the frame
        memoryPanel = new MemoryPanel();
        add(new JScrollPane(memoryTable), BorderLayout.NORTH);
        add(new JScrollPane(jobTable), BorderLayout.CENTER);
        add(memoryPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);

        // Set up event listeners
        generateMemoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMemoryBlocks();
            }
        });

        generateJobsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateJobs();
            }
        });

        firstFitRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allocateButton.setEnabled(true);
            }
        });

        nextFitRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allocateButton.setEnabled(true);
            }
        });

        bestFitRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allocateButton.setEnabled(true);
            }
        });

        worstFitRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allocateButton.setEnabled(true);
            }
        });

        allocateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allocateJobs();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetMemoryAndJobs();
            }
        });

        allocateRemainingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allocateRemainingJobs();
            }
        });

        clearAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAllData();
            }
        });

        setVisible(true);
        manager = new MemoryManager(1000); // Example total memory size
    }

    private void generateMemoryBlocks() {
        Random random = new Random();
        manager.memoryBlocks.clear();
        for (int i = 0; i < 10; i++) {
            int start = random.nextInt(900); // Ensure start + size <= 1000
            int size = random.nextInt(100) + 20; // size between 20 and 120
            manager.memoryBlocks.add(new MemoryBlock(start, size, true));
        }
        updateMemoryTable();
        memoryPanel.repaint();
        generateMemoryButton.setEnabled(false);
        resetButton.setEnabled(true);
        clearAllButton.setEnabled(true);
    }

    private void generateJobs() {
        Random random = new Random();
        manager.jobs.clear();
        for (int i = 0; i < 10; i++) {
            int size = random.nextInt(50) + 10; // size between 10 and 60
            manager.jobs.add(new Job(i, size));
        }
        updateJobTable();
        generateJobsButton.setEnabled(false);
    }

    private void allocateJobs() {
        if (firstFitRadio.isSelected()) {
            for (Job job : manager.jobs) {
                manager.firstFit(job);
            }
        } else if (nextFitRadio.isSelected()) {
            for (Job job : manager.jobs) {
                manager.nextFit(job);
            }
        } else if (bestFitRadio.isSelected()) {
            for (Job job : manager.jobs) {
                manager.bestFit(job);
            }
        } else if (worstFitRadio.isSelected()) {
            for (Job job : manager.jobs) {
                manager.worstFit(job);
            }
        }
        updateMemoryTable();
        updateJobTable();
        memoryPanel.repaint();
        allocateButton.setEnabled(false);
        allocateRemainingButton.setEnabled(true);
    }

    private void resetMemoryAndJobs() {
        manager.resetMemory();
        manager.resetJobs();
        updateMemoryTable();
        updateJobTable();
        memoryPanel.repaint();
        generateMemoryButton.setEnabled(true);
        generateJobsButton.setEnabled(true);
        allocateButton.setEnabled(false);
        resetButton.setEnabled(false);
        allocateRemainingButton.setEnabled(false);
        clearAllButton.setEnabled(false);
    }

    private void allocateRemainingJobs() {
        for (Job job : manager.jobs) {
            if (!job.isAllocated) {
                if (firstFitRadio.isSelected()) {
                    manager.firstFit(job);
                } else if (nextFitRadio.isSelected()) {
                    manager.nextFit(job);
                } else if (bestFitRadio.isSelected()) {
                    manager.bestFit(job);
                } else if (worstFitRadio.isSelected()) {
                    manager.worstFit(job);
                }
            }
        }
        updateMemoryTable();
        updateJobTable();
        memoryPanel.repaint();
    }

    private void clearAllData() {
        manager.clearAll();
        updateMemoryTable();
        updateJobTable();
        memoryPanel.repaint();
        generateMemoryButton.setEnabled(true);
        generateJobsButton.setEnabled(true);
        allocateButton.setEnabled(false);
        resetButton.setEnabled(false);
        allocateRemainingButton.setEnabled(false);
        clearAllButton.setEnabled(false);
    }

    private void updateMemoryTable() {
        String[][] memoryData = new String[manager.memoryBlocks.size()][3];
        for (int i = 0; i < manager.memoryBlocks.size(); i++) {
            MemoryBlock block = manager.memoryBlocks.get(i);
            memoryData[i][0] = String.valueOf(block.start);
            memoryData[i][1] = String.valueOf(block.size);
            memoryData[i][2] = block.isFree ? "Yes" : "No";
        }
        memoryTable.setModel(new javax.swing.table.DefaultTableModel(memoryData, new String[]{"Start", "Size", "Free"}));
    }

    private void updateJobTable() {
        String[][] jobData = new String[manager.jobs.size()][2];
        for (int i = 0; i < manager.jobs.size(); i++) {
            Job job = manager.jobs.get(i);
            jobData[i][0] = String.valueOf(job.id);
            jobData[i][1] = String.valueOf(job.size);
        }
        jobTable.setModel(new javax.swing.table.DefaultTableModel(jobData, new String[]{"ID", "Size"}));
    }

    public static void main(String[] args) {
        new MemoryManagerGUI();
    }

    class MemoryManager {
        int totalSize;
        List<MemoryBlock> memoryBlocks;
        List<Job> jobs;
        int lastAllocatedIndex = 0;

        public MemoryManager(int totalSize) {
            this.totalSize = totalSize;
            memoryBlocks = new ArrayList<>();
            jobs = new ArrayList<>();
        }

        public void firstFit(Job job) {
            for (MemoryBlock block : memoryBlocks) {
                if (block.isFree && block.size >= job.size) {
                    allocateMemory(block, job);
                    break;
                }
            }
        }

        public void nextFit(Job job) {
            int startIndex = lastAllocatedIndex;
            int size = memoryBlocks.size();
            for (int i = 0; i < size; i++) {
                int index = (startIndex + i) % size;
                MemoryBlock block = memoryBlocks.get(index);
                if (block.isFree && block.size >= job.size) {
                    allocateMemory(block, job);
                    lastAllocatedIndex = index;
                    break;
                }
            }
        }

        public void bestFit(Job job) {
            MemoryBlock bestBlock = null;
            for (MemoryBlock block : memoryBlocks) {
                if (block.isFree && block.size >= job.size) {
                    if (bestBlock == null || block.size < bestBlock.size) {
                        bestBlock = block;
                    }
                }
            }
            if (bestBlock != null) {
                allocateMemory(bestBlock, job);
            }
        }

        public void worstFit(Job job) {
            MemoryBlock worstBlock = null;
            for (MemoryBlock block : memoryBlocks) {
                if (block.isFree && block.size >= job.size) {
                    if (worstBlock == null || block.size > worstBlock.size) {
                        worstBlock = block;
                    }
                }
            }
            if (worstBlock != null) {
                allocateMemory(worstBlock, job);
            }
        }

        private void allocateMemory(MemoryBlock block, Job job) {
            if (block.size > job.size) {
                memoryBlocks.add(memoryBlocks.indexOf(block) + 1,
                        new MemoryBlock(block.start + job.size, block.size - job.size, true));
            }
            block.size = job.size;
            block.isFree = false;
            job.isAllocated = true;
            job.memoryBlock = block; // Link job to its memory block
        }

        public void resetMemory() {
            memoryBlocks.clear();
        }

        public void resetJobs() {
            jobs.clear();
        }

        public void clearAll() {
            resetMemory();
            resetJobs();
        }
    }

    class MemoryBlock {
        int start;
        int size;
        boolean isFree;

        public MemoryBlock(int start, int size, boolean isFree) {
            this.start = start;
            this.size = size;
            this.isFree = isFree;
        }
    }

    class Job {
        int id;
        int size;
        boolean isAllocated;
        MemoryBlock memoryBlock; // Reference to the allocated memory block

        public Job(int id, int size) {
            this.id = id;
            this.size = size;
            this.isAllocated = false;
        }
    }

    class MemoryPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawMemoryBlocks(g);
        }

        private void drawMemoryBlocks(Graphics g) {
            int y = 10;
            int totalMemoryHeight = 500;  // Total height allocated for memory visualization
            int memoryScale = totalMemoryHeight / manager.totalSize;

            for (MemoryBlock block : manager.memoryBlocks) {
                int blockHeight = block.size * memoryScale;
                if (block.isFree) {
                    g.setColor(Color.GREEN);
                    g.fillRect(10, y, 100, blockHeight);
                    g.setColor(Color.BLACK);
                    g.drawRect(10, y, 100, blockHeight);
                    g.drawString("Start: " + block.start + ", Size: " + block.size, 120, y + blockHeight / 2);
                } else {
                    g.setColor(Color.RED);
                    g.fillRect(10, y, 100, blockHeight);
                    g.setColor(Color.BLACK);
                    g.drawRect(10, y, 100, blockHeight);
                    g.drawString("Job ID: " + block.start + ", Size: " + block.size, 120, y + blockHeight / 2);
                }
                y += blockHeight + 10;
            }
        }
    }
}
