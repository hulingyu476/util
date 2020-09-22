# 几种任务调度的 Java 实现方法与比较
[Website](https://www.ibm.com/developerworks/cn/java/j-lo-taskschedule/)  

任务调度是指基于给定时间点，给定时间间隔或者给定执行次数自动执行任务。本文由浅入深介绍四种任务调度的 Java 实现：
* Timer
* ScheduledExecutor
* 开源工具包 Quartz
* 开源工具包 JCronTab
此外，为结合实现复杂的任务调度，本文还将介绍 Calendar 的一些使用方法。  

---
# **Timer**
相信大家都已经非常熟悉java.util.Timer了，它是最简单的一种实现任务调度的方法。
使用Timer实现任务调度的核心类是Timer和TimerTask。其中Timer负责设定TimerTask的起始于间隔执行时间。使用者只需要创建一个TimerTask的继承类，实现自己的run方法，然后将其丢过Timer去执行即可。  

Timer的设计核心是一个TaskList和一个TaskThread。Timer将接受到的任务丢到自己的TaskList中去，TaskList按照Task的最初执行时间进行排序。TimerThread在创建Timer时会自动启动成为一个守护线程。这个线程会轮询所有的任务，找到一个最近要执行的任务，然后休眠，当到达最近要执行任务的开始时间点，TimerThread被唤醒并执行该任务。之后TimerThread更新最近一个要执行的任务，继续休眠。  
Timer 的优点在于简单易用，但由于所有任务都是由同一个线程来调度，因此所有任务都是串行执行的，同一时间只能有一个任务在执行，前一个任务的延迟或异常都将会影响到之后的任务。

---
# **ScheduledExecutor**
鉴于 Timer 的上述缺陷，Java 5 推出了基于线程池设计的 ScheduledExecutor。其设计思想是，每一个被调度的任务都会由线程池中一个线程去执行，因此任务是并发执行的，相互之间不会受到干扰。需要注意的是，只有当任务的执行时间到来时，ScheduedExecutor 才会真正启动一个线程，其余时间 ScheduledExecutor 都是在轮询任务的状态。  
展示了 ScheduledExecutorService 中两种最常用的调度方法 ScheduleAtFixedRate 和 ScheduleWithFixedDelay。ScheduleAtFixedRate 每次执行时间为上一次任务开始起向后推一个时间间隔，即每次执行时间为 :initialDelay, initialDelay+period, initialDelay+2*period, …；ScheduleWithFixedDelay 每次执行时间为上一次任务结束起向后推一个时间间隔，即每次执行时间为：initialDelay, initialDelay+executeTime+delay, initialDelay+2*executeTime+2*delay。由此可见，ScheduleAtFixedRate 是基于固定时间间隔进行任务调度，ScheduleWithFixedDelay 取决于每次任务执行的时间长短，是基于不固定时间间隔进行任务调度。

---
# **用 ScheduledExecutor 和 Calendar 实现复杂任务调度**
因为Timer 和 ScheduledExecutor 都仅能提供基于开始时间与重复间隔的任务调度，不能胜任更加复杂的调度需求。比如，设置每星期二的 16:38:10 执行任务。该功能使用 Timer 和 ScheduledExecutor 都不能直接实现，但我们可以借助 Calendar 间接实现该功能。  

实现了每星期二 16:38:10 调度任务的功能。其核心在于根据当前时间推算出最近一个星期二 16:38:10 的绝对时间，然后计算与当前时间的时间差，作为调用 ScheduledExceutor 函数的参数。计算最近时间要用到 java.util.calendar 的功能。首先需要解释 calendar 的一些设计思想。Calendar 有以下几种唯一标识一个日期的组合方式：
```
YEAR + MONTH + DAY_OF_MONTH 
YEAR + MONTH + WEEK_OF_MONTH + DAY_OF_WEEK 
YEAR + MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK 
YEAR + DAY_OF_YEAR 
YEAR + DAY_OF_WEEK + WEEK_OF_YEAR
```
