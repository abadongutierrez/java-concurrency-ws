package com.jabaddon.nearsoft.javaconcurrencyws.threads;

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

    private final EmojiThreadPanel emojiThreadPanel;
    private long sleepTime = 100L;

    PingRunnable(EmojiThreadPanel emojiThreadPanel) {
        this.emojiThreadPanel = emojiThreadPanel;
    }

    @Override
    public void run() {
        // TODO: PingRunnalbe should ping `emojiThreadPanel` every `sleepTime` milliseconds
        // TODO: What should happen if it is interrupted from sleeping?
    }

    public void sleep() {
        sleepTime = TimeUnit.MILLISECONDS.convert(24, TimeUnit.HOURS);
    }
}

class EmojiThreadPanel extends JPanel implements Runnable {
    private ImageIcon newIcon = new ImageIcon(getClass().getResource("new.png"));
    private ImageIcon sleepIcon = new ImageIcon(getClass().getResource("sleeping.png"));
    private ImageIcon happyIcon = new ImageIcon(getClass().getResource("happy.png"));
    private ImageIcon killedIcon = new ImageIcon(getClass().getResource("killed.png"));
    private JButton button;
    private long lastPing;

    EmojiThreadPanel() {
        button = new JButton(newIcon);
        this.add(button);
    }

    @Override
    public void run() {
        lastPing = System.currentTimeMillis();
        while(!Thread.interrupted()) {
            // TODO: If PingRunnable haven't pinged this object since more than 500 milliseconds, `button` should show `sleepIcon'
            // TODO: If PingRunnable haven pinged this object since less than 500 milliseconds, `button` should show `happyIcon'
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        button.setIcon(killedIcon);
    }

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
        addButton(buttonPanel, "Terminate", evt -> emojiThread.interrupt());
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
