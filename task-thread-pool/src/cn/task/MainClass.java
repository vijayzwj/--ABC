package cn.task;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class MainClass {
    
    private TaskThreadPoolExecutor executor;
    
    private int minSpareThreads = 10;
    
    private int maxThreads = 200;
    
    /**
     * Running state of the endpoint.
     */
    protected volatile boolean running = false;
    
    public boolean isRunning() {
        return running;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }


    public  void createExecutor() {
        TaskQueue taskqueue = new TaskQueue();
        //Name of the thread pool, which will be used for naming child threads.
        String namePrefix="TP-exec-";
        TaskThreadFactory tf = new TaskThreadFactory(namePrefix, true, Thread.NORM_PRIORITY);
        executor = new TaskThreadPoolExecutor(minSpareThreads, maxThreads, 60, TimeUnit.SECONDS,taskqueue, tf);
        taskqueue.setParent(executor);
    }
    
    protected boolean processTask() {
        // Process the request from this socket
        try {
            TaskProcessor processor=new TaskProcessor();
            // During shutdown, executor may be null - avoid NPE
            if (!running) {
                return false;
            }
            executor.execute(processor);
        } catch (RejectedExecutionException x) {
            System.out.println("task processing request was rejected for:");
            return false;
        } catch (Throwable t) {
            System.out.println("process.fail");
            return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        
        MainClass main=new MainClass();
        
        main.createExecutor();
        
        boolean processTask = main.processTask();
        
        
    }

}
