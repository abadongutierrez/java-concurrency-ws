package com.jabaddon.nearsoft.javaconcurrencyws_solutions.threads;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

public class EmojiThread {
    public static void main(String[] args) {
        EmojiThreadFrame frame = new EmojiThreadFrame();
        frame.pack();
        frame.setVisible(true);
    }
}

class PingRunnable implements Runnable {

    private final Pingable pingable;
    private volatile long sleepTime = 100L;
    private volatile boolean shouldStop = false;

    PingRunnable(Pingable pingable) {
        this.pingable = pingable;
    }

    @Override
    public void run() {
        while (!shouldStop) {
            pingable.ping();
            try {
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            } catch (InterruptedException e) {
                sleepTime = 100L;
            }
        }
    }

    public void sleep() {
        sleepTime = TimeUnit.MILLISECONDS.convert(24, TimeUnit.HOURS);
    }

    public void stop() {
        shouldStop = true;
    }
}

interface Pingable {
    void ping();
}

class EmojiThreadPanel extends JPanel implements Runnable, Pingable {
    private ImageIcon newIcon = new ImageIcon(getClass().getResource("new.png"));
    private ImageIcon sleepIcon = new ImageIcon(getClass().getResource("sleeping.png"));
    private ImageIcon happyIcon = new ImageIcon(getClass().getResource("happy.png"));
    private ImageIcon killedIcon = new ImageIcon(getClass().getResource("killed.png"));
    private JButton button;
    private volatile long lastPing;

    EmojiThreadPanel() {
        button = new JButton(newIcon);
        this.add(button);
    }

    @Override
    public void run() {
        lastPing = System.currentTimeMillis();
        while(!Thread.currentThread().isInterrupted()) {
            long now = System.currentTimeMillis();
            if ((now - lastPing) > 500) {
                button.setIcon(sleepIcon);
            } else {
                button.setIcon(happyIcon);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        button.setIcon(killedIcon);
    }

    @Override
    public void ping() {
        lastPing = System.currentTimeMillis();
    }
}

class EmojiThreadFrame extends JFrame {
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;

    private EmojiThreadPanel emojiThreadPanel = new EmojiThreadPanel();
    private Thread emojiThread = new Thread(emojiThreadPanel);
    private PingRunnable pingRunnable = new PingRunnable(emojiThreadPanel);
    private Thread pingThread = new Thread(pingRunnable);

    EmojiThreadFrame() {
        setSize(WIDTH, HEIGHT);
        setTitle("Emoji Thread");

        Container contentPane = getContentPane();

        contentPane.add(emojiThreadPanel,  BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton(buttonPanel, "Start", evt -> {
            emojiThread.start();
            pingThread.start();
            JButton button = (JButton) evt.getSource();
            button.setEnabled(false);
        });
        addButton(buttonPanel, "Sleep", evt -> pingRunnable.sleep());
        addButton(buttonPanel, "Wake up", evt -> pingThread.interrupt());
        addButton(buttonPanel, "Terminate", evt -> {
            emojiThread.interrupt();
            pingRunnable.stop();
        });
        addButton(buttonPanel, "Close", evt -> System.exit(0));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addButton(Container c, String title,
                           ActionListener listener) {
        JButton button = new JButton(title);
        c.add(button);
        button.addActionListener(listener);
    }
}
