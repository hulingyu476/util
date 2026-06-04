/**
 * Android shell 命令执行器，支持无限个命令串型执行（需要有root权限！！）
 * <p>
 * <p>
 * HOW TO USE?
 * Example:修改开机启动动画。把/sdcard/Download目录下的bootanimation.zip文件拷贝到
 * /system/media目录下并修改bootanimation.zip的权限为777。
 * <p>
 * <pre>
 *      int result = new ShellCommandExecutor()
 *                  .setTimeout(30000)  // 设置30秒超时
 *                  .addCommand("mount -o remount,rw /system")
 *                  .addCommand("cp /sdcard/Download/bootanimation.zip /system/media")
 *                  .addCommand("cd /system/media")
 *                  .addCommand("chmod 777 bootanimation.zip")
 *                  .execute();
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
 
    public ShellCommandExecutor() {
        mCommands = new StringBuilder();
        mTimeout = DEFAULT_TIMEOUT;
    }
 
    /**
     * 设置命令执行超时时间
     * @param timeoutMs 超时时间（毫秒）
     */
    public ShellCommandExecutor setTimeout(long timeoutMs) {
        this.mTimeout = timeoutMs;
        return this;
    }
 
    public int execute() {
        return execute(mCommands.toString());
    }
 
    public ShellCommandExecutor addCommand(String cmd) {
        if (TextUtils.isEmpty(cmd)) {
            throw new IllegalArgumentException("command can not be null.");
        }
        mCommands.append(cmd);
        mCommands.append("\n");
        return this;
    }
 
    private int execute(String command) {
        int result = -1;
        DataOutputStream dos = null;
        Process process = null;
        
        try {
            Log.i(TAG, command);
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            
            dos.writeBytes(command + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            
            // 等待进程完成或超时
            boolean completed = process.waitFor(mTimeout, TimeUnit.MILLISECONDS);
            
            if (!completed) {
                // 超时：强制杀死进程，避免资源泄漏
                process.destroyForcibly();
                result = -2; // 超时标识
                Log.e(TAG, "命令执行超时，超时时间: " + mTimeout + "ms");
            } else {
                result = process.exitValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        } finally {
            // 确保资源被正确释放，避免资源泄漏
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (process != null) {
                try {
                    // 关闭所有流
                    if (process.getInputStream() != null) {
                        process.getInputStream().close();
                    }
                    if (process.getErrorStream() != null) {
                        process.getErrorStream().close();
                    }
                    if (process.getOutputStream() != null) {
                        process.getOutputStream().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 销毁进程
                process.destroy();
            }
        }
        
        return result;
    }
}
