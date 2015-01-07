import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GUI extends JPanel implements Runnable, ActionListener{

    private static Thread MiliTimerThread;

    private static GUI newContentPane;

    private long milisbefore = 0;

    public static int n_milis = 0 ,n_secs = 0 ,n_mins = 0 ,n_hrs = 0;

    private JFrame frame = new JFrame("Timer");

    protected static JTextArea hrs, mins, secs, milis;
    protected static JButton Start;
    protected static JButton Stop;
    protected static JButton pauseresume;
    protected static JButton reset;
    static boolean running = false;
    static boolean countup = false; //false if it's countdown - duh!
    boolean paused = false;

    protected static long LatestTime = 0;

    public static synchronized void update(){
        if (countup) {                            //Timer
            int timeDiff = millisecDiff();
            n_milis = n_milis + timeDiff;
            if (n_milis >= 1000) {
                n_milis = n_milis - 1000;
                n_secs++;
                if (n_secs >= 60) {
                    n_secs = n_secs - 60;
                    n_mins++;
                    if (n_mins >= 60) {
                        n_hrs++;
                        n_mins = n_mins - 60;
                        hrs.setText(String.valueOf(n_hrs));
                    }
                    mins.setText(String.valueOf(n_mins));
                }
                secs.setText(String.valueOf(n_secs));
            }
            milis.setText(String.valueOf(n_milis));
        } else {                                       //Countdown
            int timeDiff = millisecDiff();
            n_milis = n_milis - timeDiff;
            if (n_milis <= 0){
                n_milis = n_milis + 1000;
                if (n_secs <= 0){
                    n_secs = n_secs + 60;
                    if (n_mins <= 0){
                        n_mins = n_mins + 60;
                        if (n_hrs <= 0){
                            running = false;
                            MiliTimerThread.interrupt();
                            try {
                                Clip clip = AudioSystem.getClip();
                                AudioInputStream inputStream = AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("ding.wav"));
                                clip.open(inputStream);
                                clip.start();
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                            n_milis = 0;
                            n_secs = 0;
                            n_mins = 0;
                            n_hrs = 0;
                            milis.setText("000");
                            secs.setText("00");
                            mins.setText("00");
                            hrs.setText("00");
                            milis.setEditable(true);
                            secs.setEditable(true);
                            mins.setEditable(true);
                            hrs.setEditable(true);
                            Stop.setEnabled(false);
                            reset.setEnabled(false);
                            Start.setEnabled(true);
                            pauseresume.setEnabled(false);
                            return;
                        }
                        n_hrs--;
                        hrs.setText(String.valueOf(n_hrs));
                    }
                    n_mins--;
                    mins.setText(String.valueOf(n_mins));
                }
                n_secs--;
                secs.setText(String.valueOf(n_secs));
            }
            milis.setText(String.valueOf(n_milis));
        }
    }

    public static Integer millisecDiff(){
        int timeDiff = (int) (System.currentTimeMillis() - LatestTime);
        LatestTime = System.currentTimeMillis();
        if (timeDiff < 0) timeDiff = (int) Math.pow(Math.pow(timeDiff, 2), 0.5);  //Make it positive
        return timeDiff;
    }

    public static synchronized void addmili(){
        n_milis++;
        if (n_milis > 999){
            int tempnum = n_milis - 999;
            n_milis = tempnum - 1;
            //addsec();
        }
        if ((n_milis % 3)==0) {
            milis.setText(String.valueOf(n_milis));
        }
    }



    public static synchronized void addsec(){
        n_secs++;
        if (n_secs == 60) {
            n_secs = 0;
            //addmin();
        }
        secs.setText(String.valueOf(n_secs));
        n_milis = 0;
        milis.setText("0");
        MiliTimerThread.interrupt();
        MiliTimerThread = new Thread(new MilliTimer());
        MiliTimerThread.start();
    }

    private static final Insets insets = new Insets(0, 0, 0, 0);

    public String[] Words;

    public Font getFont(int size){
        return new Font("Verdana", Font.BOLD, size);
    }

    public GUI(){
        hrs = new JTextArea("00");
        hrs.setFont(getFont(150));

        mins = new JTextArea("00");
        mins.setFont(getFont(150));

        secs = new JTextArea("00");
        secs.setFont(getFont(150));

        milis = new JTextArea("000");
        milis.setFont(getFont(50));
        /*milis.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        */

        Start = new JButton("Start");
        Start.setFont(getFont(40));
        Start.setActionCommand("start");
        Start.addActionListener(this);

        Stop = new JButton("Stop");
        Stop.setFont(getFont(40));
        Stop.setEnabled(false);
        Stop.setActionCommand("stop");
        Stop.addActionListener(this);

        pauseresume = new JButton("Pause"); // /Resume
        pauseresume.setFont(getFont(40));
        pauseresume.setEnabled(false);
        pauseresume.setActionCommand("pause");  //Also resumes
        pauseresume.addActionListener(this);

        reset = new JButton("Reset");
        reset.setFont(getFont(40));
        reset.setEnabled(true);
        reset.setActionCommand("reset");
        reset.addActionListener(this);

        GridBagLayout gridBagLayout = new GridBagLayout();
        frame.setLayout(gridBagLayout);

        addComponent(frame,hrs,0,0,5,5,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        addComponent(frame,mins,5,0,5,5,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        addComponent(frame,secs,10,0,5,5,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        addComponent(frame,milis,15,0,3,3,GridBagConstraints.SOUTHWEST,GridBagConstraints.BOTH);

        addComponent(frame,Start,0,5,3,5,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        addComponent(frame,Stop,5,5,3,5,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        addComponent(frame,pauseresume,10,5,3,5,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        addComponent(frame,reset,15,5,3,5,GridBagConstraints.CENTER,GridBagConstraints.BOTH);

        frame.setSize(1000, 300);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void addComponent(Container container, Component component, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill) {
            GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0, anchor,0, insets, 0, 0);
            container.add(component, gbc);
    }

    public void run(){
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {        //ToDo:"start" will fire when the start button is hit
        String Action = e.getActionCommand();                          //ToDo: Check to see if the boxes are default (if yes, stopwatch, if no, timer with the contents)
        if (Action.equalsIgnoreCase("start")){                         //ToDo: Adjust dims?
            if (milis.getText().equalsIgnoreCase("000")&&
                    secs.getText().equalsIgnoreCase("00")&&
                    mins.getText().equalsIgnoreCase("00")&&
                    hrs.getText().equalsIgnoreCase("00")){
                MiliTimerThread = new Thread(new MilliTimer());
                MiliTimerThread.start();
                milisbefore = System.currentTimeMillis();
                LatestTime = System.currentTimeMillis();
                milis.setEditable(false);
                secs.setEditable(false);
                mins.setEditable(false);
                hrs.setEditable(false);
                Start.setEnabled(false);
                Stop.setEnabled(true);
                pauseresume.setEnabled(true);
                reset.setEnabled(false);
                running = true;
                countup = true;
            } else {                                    //countdown
                MiliTimerThread = new Thread(new MilliTimer());
                MiliTimerThread.start();
                milisbefore = System.currentTimeMillis();
                LatestTime = System.currentTimeMillis();
                milis.setEditable(false);
                secs.setEditable(false);
                mins.setEditable(false);
                hrs.setEditable(false);
                n_milis = Integer.valueOf(milis.getText());
                n_secs = Integer.valueOf(secs.getText());
                n_mins = Integer.valueOf(mins.getText());
                n_hrs = Integer.valueOf(hrs.getText());
                Start.setEnabled(false);
                Stop.setEnabled(true);
                pauseresume.setEnabled(true);
                reset.setEnabled(false);
                running = true;
                countup = false;
                if (n_milis == 0 && n_secs == 30 && n_mins == 0 && n_hrs == 0){
                    try {
                        Clip clip = AudioSystem.getClip();
                        AudioInputStream inputStream = AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("countdown.wav"));
                        clip.open(inputStream);
                        clip.start();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }

        }
        if (Action.equalsIgnoreCase("Stop")){
            Stop.setEnabled(false);
            Start.setEnabled(false);
            pauseresume.setEnabled(false);
            reset.setEnabled(true);
            MiliTimerThread.interrupt();
            update();
        }
        if (Action.equalsIgnoreCase("reset")){
            n_milis = 0;
            n_secs = 0;
            n_mins = 0;
            n_hrs = 0;
            milis.setText("000");
            secs.setText("00");
            mins.setText("00");
            hrs.setText("00");
            milis.setEditable(true);
            secs.setEditable(true);
            mins.setEditable(true);
            hrs.setEditable(true);
            reset.setEnabled(false);
            Start.setEnabled(true);
            pauseresume.setEnabled(false);
        }
        if (Action.equalsIgnoreCase("pause")){
            if (paused){
                pauseresume.setText("Pause");
                Stop.setEnabled(true);
                MiliTimerThread = new Thread(new MilliTimer());
                MiliTimerThread.start();
                LatestTime = System.currentTimeMillis();
            } else {
                pauseresume.setText("Resume");
                MiliTimerThread.interrupt();
                Stop.setEnabled(false);
                update();
            }
            paused = !paused;
        }
    }
}
