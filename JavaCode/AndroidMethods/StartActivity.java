/**
* 跳转到指定应用的首页
*/
public void showActivity(String packageName,Context context){
    Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
    context.startActivity(intent);
}


/**
*  跳转到指定应用的指定页面
* */
public void showActivity(String packageName,String activityDir,Context context){
    Intent intent = new Intent();
    intent.setComponent(new ComponentName(packageName, activityDir));
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
}
