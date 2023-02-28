# 背景
1. 不支持的USB格式，如exFAT,NTFS
2. 拷贝文件的时候，有的APK处理不了，会报/dev/null错误

# 解决方法
在文件StorageManagerService.java
```java
public StorageVolume[] getVolumeList(int uid, String packageName, int flags) {
        final StorageVolume userVol = vol.buildStorageVolume(mContext, userId,reportUnmounted);

    //============================  add ==================================
    if("com.mobisystems.fileman".equals(packageName) && userVol.getPath().startsWith("/dev/null")) {
        //Slog.e(TAG, "for FileCommander, remove /dev/null");
        continue;
    }
    //==================================================================
}
```