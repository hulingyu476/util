//Method1 Thread
new Thread(new Runnable(){
    @Override
    public void run(){
        Thread.sleep(1000); //ms
        //handler.sendMessage();
        //do something
    }
}).start();


//Method2 TimeTask
TimeTask task = new TimeTask(){
    @Override
    public void run(){
        //do something;
    }
};
Timer timer = new Timer();
timer.schedule(task,2000);


//suggest
//Android message 
new Handler().postDelayed(new Runnable(){
    public void run(){
        //do something
    }
}, 3000);