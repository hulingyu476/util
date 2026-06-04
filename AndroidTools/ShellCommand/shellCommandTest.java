/**
 * Android shell 命令执行器，支持无限个命令串型执行（需要有root权限！！）
 * 改进版本，添加超时控制、详细日志、失败停止等功能
 * <p>
 * <p>
 * HOW TO USE?
 * Example:修改开机启动动画。把/sdcard/Download目录下的bootanimation.zip文件拷贝到
 * /system/media目录下并修改bootanimation.zip的权限为777。
 * <p>
 * <pre>
 *      ShellCommandExecutor executor = new ShellCommandExecutor()
 *                  .setTimeout(30000)  // 设置30秒超时
 *                  .setStopOnError(true)  // 命令失败时停止
 *                  .addCommand("mount -o remount,rw /system")
 *                  .addCommand("cp /sdcard/Download/bootanimation.zip /system/media")
 *                  .addCommand("cd /system/media")
 *                  .addCommand("chmod 777 bootanimation.zip");
 *      ShellResult result = executor.execute();
 *      String output = executor.getOutput();
 * <pre/>
 *
 * @author silas.
 */

import java.io.*;
import java.util.concurrent.*;
 
public class ShellCommandExecutor {
    private static final String TAG = "ShellCommandExecutor";
    private static final long DEFAULT_TIMEOUT = 30000; // 默认超时30秒
 
    private StringBuilder mCommands;
    private long mTimeout;
    private boolean mStopOnError;
    private StringBuilder mOutput;
    private StringBuilder mError;
 
    public ShellCommandExecutor() {
        mCommands = new StringBuilder();
        mOutput = new StringBuilder();
        mError = new StringBuilder();
        mTimeout = DEFAULT_TIMEOUT;
        mStopOnError = true;
    }
 
    /**
     * 设置命令执行超时时间
     * @param timeoutMs 超时时间（毫秒）
     */
    public ShellCommandExecutor setTimeout(long timeoutMs) {
        this.mTimeout = timeoutMs;
        return this;
    }
 
    /**
     * 设置是否在命令失败时停止执行
     * @param stopOnError true表示失败时停止
     */
    public ShellCommandExecutor setStopOnError(boolean stopOnError) {
        this.mStopOnError = stopOnError;
        return this;
    }
 
    /**
     * 添加要执行的命令
     */
    public ShellCommandExecutor addCommand(String cmd) {
        if (TextUtils.isEmpty(cmd)) {
            throw new IllegalArgumentException("command can not be null.");
        }
        mCommands.append(cmd);
        mCommands.append("\n");
        return this;
    }
 
    /**
     * 执行所有命令
     * @return ShellResult 包含返回码、输出、错误信息
     */
    public ShellResult execute() {
        mOutput = new StringBuilder();
        mError = new StringBuilder();
        return executeCommand(mCommands.toString());
    }
 
    /**
     * 获取命令的标准输出
     */
    public String getOutput() {
        return mOutput.toString();
    }
 
    /**
     * 获取命令的错误输出
     */
    public String getError() {
        return mError.toString();
    }
 
    /**
     * 执行单条命令
     */
    private ShellResult executeCommand(String command) {
        ShellResult result = new ShellResult();
        DataOutputStream dos = null;
        InputStream inputStream = null;
        InputStream errorStream = null;
        Process process = null;
 
        try {
            Log.i(TAG, "执行命令: " + command);
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            inputStream = process.getInputStream();
            errorStream = process.getErrorStream();
 
            // 写入命令
            dos.writeBytes(command + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
 
            // 创建线程读取输出和错误流，避免缓冲区阻塞
            Thread outputThread = new Thread(new StreamReader(inputStream, mOutput, "OUTPUT"));
            Thread errorThread = new Thread(new StreamReader(errorStream, mError, "ERROR"));
            outputThread.start();
            errorThread.start();
 
            // 等待进程完成或超时
            boolean completed = process.waitFor(mTimeout, TimeUnit.MILLISECONDS);
 
            if (!completed) {
                process.destroyForcibly();
                result.exitCode = -2;
                result.errorMsg = "命令执行超时（" + mTimeout + "ms）";
                Log.e(TAG, result.errorMsg);
                return result;
            }
 
            // 等待读取线程完成
            outputThread.join(1000);
            errorThread.join(1000);
 
            result.exitCode = process.exitValue();
            result.output = mOutput.toString();
            result.error = mError.toString();
 
            if (result.exitCode != 0) {
                result.errorMsg = "命令执行失败，返回码: " + result.exitCode;
                Log.e(TAG, result.errorMsg);
                if (mStopOnError) {
                    Log.w(TAG, "检测到错误，停止执行后续命令");
                }
            } else {
                Log.i(TAG, "命令执行成功");
            }
 
            return result;
 
        } catch (InterruptedException e) {
            result.exitCode = -3;
            result.errorMsg = "命令执行被中断: " + e.getMessage();
            Log.e(TAG, result.errorMsg, e);
            if (process != null) {
                process.destroyForcibly();
            }
            return result;
        } catch (IOException e) {
            result.exitCode = -1;
            result.errorMsg = "执行命令出错: " + e.getMessage();
            Log.e(TAG, result.errorMsg, e);
            return result;
        } catch (Exception e) {
            result.exitCode = -1;
            result.errorMsg = "未知错误: " + e.getMessage();
            Log.e(TAG, result.errorMsg, e);
            return result;
        } finally {
            closeResource(dos);
            closeResource(inputStream);
            closeResource(errorStream);
            if (process != null) {
                process.destroy();
            }
        }
    }
 
    /**
     * 关闭资源
     */
    private void closeResource(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                Log.w(TAG, "关闭资源失败: " + e.getMessage(), e);
            }
        }
    }
 
    /**
     * 流读取器，用于读取进程的输出和错误流
     */
    private static class StreamReader implements Runnable {
        private InputStream inputStream;
        private StringBuilder output;
        private String streamType;
 
        public StreamReader(InputStream inputStream, StringBuilder output, String streamType) {
            this.inputStream = inputStream;
            this.output = output;
            this.streamType = streamType;
        }
 
        @Override
        public void run() {
            if (inputStream == null) {
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            } catch (IOException e) {
                Log.e(TAG, "读取" + streamType + "失败: " + e.getMessage(), e);
            }
        }
    }
 
    /**
     * 命令执行结果类
     */
    public static class ShellResult {
        public int exitCode;        // 返回码
        public String output;       // 标准输出
        public String error;        // 错误输出
        public String errorMsg;     // 错误消息
 
        @Override
        public String toString() {
            return "ShellResult{" +
                    "exitCode=" + exitCode +
                    ", output='" + output + '\'' +
                    ", error='" + error + '\'' +
                    ", errorMsg='" + errorMsg + '\'' +
                    '}';
        }
    }
}
