package me.jor.pool;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
/**
   pool.tasksQueue.size()>0 && (size<pool.minSize || (size>=pool.minSize && size<pool.maxSize))是true时创建新线程
   池内的线程运行ThreadPool的一个Runnable内部类的单例对象，
   此对象的run方法是个可从外部通过修改属性来退出的循环
   每次循环开始时都会判断是否需要创建新线程
   如果不需要就从任务队列取出任务并执行
   如果需要创建新线程就执行一个循环用于创建新的线程并把新线程添加到池中，
   在从任务队列取得任务之后或创建新线程之前都从池中激活另一个线程用于继续尝试从任务队列中取得任务
   任务执行结束或创建新线程的条件不再满足时线程会调用自身的wait(),并返回池中。
   或者tasksQueue.size()==0 && working.size()>minSize，此条件满足时当前线程会销毁并不再返回池
   执行任务抛出异常时会在异常处理结束后，继续尝试执行下一个任务
 * @author running
 *
 */
public class ThreadPool implements Executor {
    
    private int minSize;
    private int maxSize;
    private int idelTime;//每个线程的空闲时间，毫秒数
    private volatile Queue<ThreadInPool> working;
    private volatile BlockingQueue<Runnable> tasksQueue;
    private Runnable runnable;
    
    public ThreadPool(int minSize, int maxSize, int idelTime){
        this(minSize, maxSize, idelTime, new LinkedBlockingQueue<Runnable>());
    }
    public ThreadPool(int minSize, int maxSize, int idelTime, BlockingQueue<Runnable> tasksQueue){
        this.idelTime=idelTime;
        this.maxSize=maxSize;
        this.minSize=minSize;
        this.tasksQueue=tasksQueue;
        this.working=new LinkedList<ThreadInPool>();
        final ThreadPool pool=this;
        runnable=new Runnable(){
            private volatile boolean increasing;
            @Override
            public void run(){
                ThreadInPool thread=(ThreadInPool)Thread.currentThread();
                while(thread.started){
                    if(pool.toIncrease() && !increasing){
                        synchronized(working){
                            if(pool.toIncrease() && !increasing){
                                pollExec();
                                while(pool.toIncrease()){
                                    increasing=true;
                                    pool.increase();
                                }
                                increasing=false;
                                pool.returnToPool();
                            }else{
                                pool.execute();
                            }
                        }
                    }else{
                        pool.execute();
                    }
                }
            }
        };
    }
    private void pollExec(){
	    ThreadInPool tip=working.poll();
	    if(tip!=null){
	    	tip.execute();
	    }
    }
    private void execute(){
        try {
            Runnable runnable = tasksQueue.take();
            if(runnable!=null){
                pollExec();
                runnable.run();
            }
            synchronized(working){
                if(tasksQueue.size()==0 && working.size()>minSize){
                   synchronized(working){
                        decrease();
                    }
                }
            }
            returnToPool();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void returnToPool(){
     Thread current=Thread.currentThread();
        synchronized(working){
            working.offer((ThreadInPool)current);
        }
        synchronized(current){
            try{
                current.wait();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private boolean toIncrease(){
        int size=working.size();
        return tasksQueue.size()>0 && (size<minSize || (size>=minSize && size<maxSize));
    }
    protected void increase(){
        this.working.add(new ThreadInPool(runnable));
    }
    protected void decrease(){
       for(;tasksQueue.size()==0 && working.size()>minSize;){
          ThreadInPool thread=(ThreadInPool)working.poll();
          thread.started=false;
          thread.notify();
       }
    }
    
    /**
     * 仅仅把任务加入任务队列中。
     */
    @Override
    public void execute(Runnable command) {
        tasksQueue.offer(command);
    }
    /**
     * 仅仅把任务加入任务队列中。
     * @param blocking  如果值是true且任务队列已满，就等待直到队列可用。等待期间如果被中断会抛出InterruptedException
     *                  如果值是false且任务队列已满，会立即返回false
     */
    public boolean execute(Runnable command, boolean blocking) throws InterruptedException{
        if(blocking){
            tasksQueue.put(command);
            return true;
        }else{
            return tasksQueue.offer(command);
        }
    }
    public int getIdelTime() {
        return idelTime;
    }
    public void setIdelTime(int idelTime) {
        this.idelTime = idelTime;
    }
    
    
    
    private static class ThreadInPool extends Thread{
        private static AtomicInteger id=new AtomicInteger(0);
        private volatile boolean started;
        private int hash=id.getAndIncrement();
        public ThreadInPool(Runnable runnable){
            super(runnable);
        }
        @Override
        public int hashCode(){
            return hash;
        }
        @Override
        public boolean equals(Object o){
            return o.hashCode()==this.hashCode();
        }
        public void execute(){
            if(started){
                synchronized(this){
                    this.notify();
                }
            }else{
                start();
                started=true;
            }
        }
    }
}