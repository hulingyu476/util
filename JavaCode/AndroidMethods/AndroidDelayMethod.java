/**
 * Android 延迟执行的几种方法对比
 * 
 * 总体推荐: 在 Android 应用中使用 Handler.postDelayed()，因为它不会阻塞 UI 线程
 */

// ============================================================
// Method1: Thread.sleep() - 不推荐（会阻塞线程）
// ============================================================

// ❌ 原始方式（匿名内部类）
new Thread(new Runnable() {
    @Override
    public void run() {
        try {
            Thread.sleep(1000); // ms
            // do something
            System.out.println("延迟1秒后执行");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}).start();

// ✅ Lambda 格式（Java 8+）
new Thread(() -> {
    try {
        Thread.sleep(1000); // ms
        // do something
        System.out.println("延迟1秒后执行");
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        e.printStackTrace();
    }
}).start();

// 优点: 简单直观
// 缺点: 创建新线程，开销较大；如果在主线程调用会卡UI；异常需要手动处理






// ============================================================
// Method2: Timer + TimerTask - 不推荐（不是Android推荐方式）
// ============================================================

// ❌ 原始方式（匿名内部类）
TimerTask task = new TimerTask() {
    @Override
    public void run() {
        // do something
        System.out.println("延迟2秒后执行");
    }
};
Timer timer = new Timer();
timer.schedule(task, 2000); // ms

// ✅ Lambda 格式（Java 8+）
Timer timer = new Timer();
timer.schedule(new TimerTask() {
    @Override
    public void run() {
        System.out.println("延迟2秒后执行");
    }
}, 2000);

// 或者更简洁的写法（创建匿名TimerTask的lambda是不支持的，因为TimerTask是abstract class）
// 但可以这样写：
Timer timer2 = new Timer();
timer2.schedule(() -> System.out.println("延迟2秒后执行"), 2000); // 仅限于支持函数式接口的情况

// 优点: 支持重复执行、可以取消
// 缺点: 创建后台线程；不适合 Android UI 更新；需要手动管理生命周期







// ============================================================
// Method3: Handler.postDelayed() - ✅ Android 官方推荐
// ============================================================

// ❌ 原始方式（匿名内部类）
new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {
        // do something
        System.out.println("延迟3秒后执行");
    }
}, 3000); // ms

// ✅ Lambda 格式（Java 8+）
new Handler().postDelayed(() -> {
    // do something
    System.out.println("延迟3秒后执行");
}, 3000);

// 或者使用 Looper（推荐指定线程）
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    System.out.println("在主线程延迟3秒后执行");
}, 3000);

// 优点: 
//   - 在指定的 Looper 线程上执行（通常是主线程）
//   - 自动管理生命周期
//   - 避免线程创建开销
//   - 可以取消任务
// 缺点: 
//   - 需要 Handler 对象


// ============================================================
// Method4: View.postDelayed() - ✅ UI 操作最佳方案
// ============================================================

// ✅ Lambda 格式（在任何 View 对象上调用）
view.postDelayed(() -> {
    // do something
    System.out.println("在主线程延迟3秒后执行");
}, 3000);

// 优点: 
//   - 自动在主线程执行
//   - 最简洁的写法
//   - 与 View 生命周期绑定


// ============================================================
// Method5: Thread.sleep() in AsyncTask - 不推荐
// ============================================================

// ❌ 过时方式（AsyncTask 已弃用）
new AsyncTask<Void, Void, Void>() {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(1000);
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        System.out.println("延迟1秒后执行");
    }
}.execute();

// 缺点: AsyncTask 已被 Google 弃用，推荐使用 Coroutines 或 RxJava


// ============================================================
// Method6: Coroutines - ✅ 现代最佳方案（Kotlin）
// ============================================================

// 在 Kotlin 中使用协程（推荐）
// GlobalScope.launch {
//     delay(1000)
//     // do something
//     println("延迟1秒后执行")
// }
//
// 或者使用 lifecycleScope（推荐）
// lifecycleScope.launch {
//     delay(1000)
//     println("延迟1秒后执行")
// }


// ============================================================
// 总结对比表
// ============================================================

/*
┌─────────────────┬────────────┬──────────┬────────────┬─────────┐
│ 方法             │ 线程安全   │ 易用性   │ 推荐度     │ 场景    │
├─────────────────┼────────────┼──────────┼────────────┼─────────┤
│ Thread.sleep()  │ ✓          │ ★★★     │ ★         │ 后台任务│
│ Timer           │ ✓          │ ★★      │ ★★        │ 定时任务│
│ Handler         │ ✓          │ ★★★     │ ★★★★      │ UI更新  │
│ View.postDelayed│ ✓          │ ★★★★   │ ★★★★★    │ UI更新  │
│ AsyncTask       │ ✓          │ ★★      │ ★         │ 已弃用  │
│ Coroutines      │ ✓          │ ★★★★   │ ★★★★★    │ 现代方案│
└─────────────────┴────────────┴──────────┴────────────┴─────────┘
*/