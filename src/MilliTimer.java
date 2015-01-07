
public class MilliTimer implements Runnable{

    boolean cont = true;

    public MilliTimer(){}

    public void run(){
        while (cont){
            Wait();
            if (!cont) return;
            GUI.update();
        }
    }


    synchronized void Wait(){
        try {
            Thread.currentThread().sleep(17);
        } catch (Exception ex){cont = false;}
    }
}
