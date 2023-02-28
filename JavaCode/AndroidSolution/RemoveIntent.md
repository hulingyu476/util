# 问题说明
1. 有些apk的AndroidManifest.xml里面写了一些intent，但是系统不希望apk处理这个intent
2. 且没有apk的源码的情况下
3. 通过pms解析intent的方法实现

# 方法代码
1. PackageManagerService.java的applyPostResolutionFilter方法中
```java
private List<ResolveInfo> applyPostResolutionFilter(@NonNull List<ResolveInfo> resolveInfos,
        String ephemeralPkgName, boolean allowDynamicSplits, int filterCallingUid,
        boolean resolveForStart, int userId, Intent intent) {
        ///// some code 
        final ResolveInfo info = resolveInfos.get(i);
        //======================================================================================
        //  remove disabled media activity
        //======================================================================================
        if(!TextUtils.isEmpty(intentDataType)) {
            Slog.i(TAG, "ResolveInfo: " + info + " type = " + intentDataType);


            List<String> disabledList = new ArrayList<>();
            if(intentDataType.startsWith("video")) {
                disabledList = disabledVideoActivityList;
            } else if(intentDataType.startsWith("audio")) {
                disabledList = disabledAudioActivityList;
            } else if(intentDataType.startsWith("image")) {
                disabledList = disabledImageActivityList;
            }

            if(disabledList.contains(info.getComponentInfo().packageName + "/" + info.getComponentInfo().name)) {
                resolveInfos.remove(i);
                continue;
            }
        }
}
//list add package at here
//==============================================================================================
public static List<String> disabledVideoActivityList = new ArrayList<>();
public static List<String> disabledAudioActivityList = new ArrayList<>();
public static List<String> disabledImageActivityList = new ArrayList<>();
static {
    // 禁止该 Activity 打开图片
    disabledImageActivityList.add("mediatek.factorymenu.ui" + "/" + "mediatek.tvsetting.factory.ui.factorymenu.testpatternforpanel.TestImagePatternsActivity");
    disabledImageActivityList.add("com.mobisystems.office" + "/" + "com.mobisystems.libfilemng.FcOfficeFiles");


    // 禁止该 Activity 打开音频
    disabledAudioActivityList.add("com.mobisystems.office" + "/" + "com.mobisystems.libfilemng.FcOfficeFiles");
    disabledAudioActivityList.add("com.mobisystems.office" + "/" + "com.mobisystems.office.EditorLauncher");
}
//==============================================================================================
```