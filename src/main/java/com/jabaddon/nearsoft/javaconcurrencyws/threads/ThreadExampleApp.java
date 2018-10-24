package com.jabaddon.nearsoft.javaconcurrencyws.threads;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ThreadExampleApp {
    public static void main(String[] args) {
        Frame frame = new Frame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

class RunnableThreadRowPanel extends JPanel implements Runnable {
    private static final int MAX_VALUE = 1000;
    private final int id;
    private final int priority;
    private final Map<Integer, Long> hintsMap;
    private final RunnableThreadRowPanelFinder threadFinder;
    private final AtomicInteger atomicInteger = new AtomicInteger(100);
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel sleepTimeLabel;
    private Thread threadInstance;
    private Thread threadToJoin;

    RunnableThreadRowPanel(int id, int priority, Map<Integer, Long> hintsMap, RunnableThreadRowPanelFinder threadFinder) {
        this.id = id;
        this.priority = priority;
        this.hintsMap = hintsMap;
        this.threadFinder = threadFinder;
        this.setLayout(new FlowLayout());

        int compHeight = 30;
        JLabel idLabel = new JLabel();
        idLabel.setText(String.valueOf(id));
        idLabel.setPreferredSize(new Dimension(20, compHeight));
        this.add(idLabel);

        progressBar = new JProgressBar(0, MAX_VALUE);
        progressBar.setPreferredSize(new Dimension(300, compHeight));
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        this.add(progressBar);

        JPanel joinPanel = new JPanel();
        joinPanel.setBorder(new BorderUIResource.BevelBorderUIResource(1));
        JTextField joinTextField = new JTextField("");
        joinTextField.setPreferredSize(new Dimension(30, compHeight - 10));
        JButton joinButton = new JButton("Join");
        joinButton.setPreferredSize(new Dimension(30, compHeight - 10));
        joinPanel.add(joinTextField);
        joinPanel.add(joinButton);
        this.add(joinPanel);

        joinButton.addActionListener(evt -> {
            String joinIdStr = joinTextField.getText();
            try {
                int joinId = Integer.parseInt(joinIdStr);
                Optional<RunnableThreadRowPanel> threadById = threadFinder.findThreadById(joinId);
                if (threadById.isPresent()) {
                    threadToJoin = threadById.get().getThreadInstance();
                }

            } catch (NumberFormatException ex) {
                joinTextField.setText("");
            }
        });

        JButton button = new JButton("Int");
        button.addActionListener(evt -> {
            if (threadInstance != null && !threadInstance.isInterrupted()) {
                threadInstance.interrupt();
            }
        });
        button.setPreferredSize(new Dimension(30, compHeight - 10));
        this.add(button);

        JPanel sleepPanel = new JPanel();
        sleepPanel.setBorder(new BorderUIResource.BevelBorderUIResource(1));
        sleepTimeLabel = new JLabel();
        updateSleepTimeLabel();
        sleepTimeLabel.setPreferredSize(new Dimension(30, compHeight));
        sleepPanel.add(sleepTimeLabel);

        JButton plusButton = new JButton("+");
        plusButton.addActionListener(evt -> atomicInteger.addAndGet(10));
        plusButton.setPreferredSize(new Dimension(30, compHeight - 10));
        sleepPanel.add(plusButton);

        JButton minusButton = new JButton("-");
        minusButton.addActionListener(evt -> atomicInteger.addAndGet(-10));
        minusButton.setPreferredSize(new Dimension(30, compHeight - 10));
        sleepPanel.add(minusButton);

        this.add(sleepPanel);

        statusLabel = new JLabel();
        statusLabel.setText(Thread.State.NEW.name());
        statusLabel.setBorder(new BorderUIResource.BevelBorderUIResource(1));
        statusLabel.setPreferredSize(new Dimension(200, compHeight));
        this.add(statusLabel);
    }

    @Override
    public void run() {
        while (!threadInstance.isInterrupted()) {
            joinThreadIfAny();
            // compute next hint
            hintsMap.compute(id, (k, v) -> v == null ? 0L : v + 1L);
            Long value = hintsMap.get(id);
            // update progress bar
            progressBar.setValue(Math.toIntExact(value));
            // update sleep time and thread state
            updateStatus();

            if (value > MAX_VALUE) {
                threadInstance.interrupt();
            }

            try {
                //TimeUnit.MILLISECONDS.sleep(atomicInteger.get());
                Thread.sleep(atomicInteger.get());
            } catch (InterruptedException e) {
                threadInstance.interrupt();
            }
        }
    }

    private void joinThreadIfAny() {
        if (threadToJoin != null) {
            try {
                threadToJoin.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadToJoin = null;
        }
    }

    public int getPriority() {
        return priority;
    }

    public int getId() {
        return id;
    }

    public void setThreadInstance(Thread threadInstance) {
        this.threadInstance = threadInstance;
    }

    public Thread getThreadInstance() {
        return threadInstance;
    }

    public void updateStatus() {
        updateThreadStatus();
        updateSleepTimeLabel();
    }

    private void updateThreadStatus() {
        if (threadInstance != null) {
            statusLabel.setText(threadInstance.getState().name());
        }
    }

    private void updateSleepTimeLabel() {
        sleepTimeLabel.setText(String.valueOf(atomicInteger.get()));
    }
}

interface RunnableThreadRowPanelFinder {
    Optional<RunnableThreadRowPanel> findThreadById(int id);
}

class Frame extends JFrame implements RunnableThreadRowPanelFinder {
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;

    private final Map<Integer, Long> hintsMap = new ConcurrentHashMap<>();
    private final List<RunnableThreadRowPanel> runnableThreadRowPanelList = new ArrayList<>();

    Frame() {
        setSize(WIDTH, HEIGHT);
        setTitle("ThreadPriority");

        Container contentPane = getContentPane();

        JPanel progressBarPanel = new JPanel();
        progressBarPanel.setLayout(new BoxLayout(progressBarPanel, BoxLayout.PAGE_AXIS));
        contentPane.add(progressBarPanel, BorderLayout.CENTER);

        IntStream.range(1, 10 + 1).forEach(id -> {
            RunnableThreadRowPanel runnableThreadRowPanel = new RunnableThreadRowPanel(id, id, hintsMap, this);
            runnableThreadRowPanelList.add(runnableThreadRowPanel);
            progressBarPanel.add(runnableThreadRowPanel, BorderLayout.PAGE_START);
        });

        JPanel buttonPanel = new JPanel();
        addButton(buttonPanel, "Start", evt -> {
            startThreads();
            JButton button = (JButton) evt.getSource();
            button.setEnabled(false);
        });
        addButton(buttonPanel, "Close", evt -> System.exit(0));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        updateThreadStatuses();
    }

    private void updateThreadStatuses() {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                runnableThreadRowPanelList.forEach(p -> p.updateStatus());
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void startThreads() {
        runnableThreadRowPanelList.forEach(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setPriority(runnable.getPriority());
            runnable.setThreadInstance(thread);
            thread.start();
        });
    }

    private void addButton(Container c, String title,
                           ActionListener listener) {
        JButton button = new JButton(title);
        c.add(button);
        button.addActionListener(listener);
    }

    @Override
    public Optional<RunnableThreadRowPanel> findThreadById(int id) {
        return runnableThreadRowPanelList.stream().filter(p -> p.getId() == id).findFirst();

    }
}