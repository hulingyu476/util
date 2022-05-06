//软件混音：系统Framework层将第三方导航的声音绑定到STREAM_ALARM通道， 系统媒体声音绑定到STREAM_MUSIC通道，通过设置不同通道的音量来实现混音效果。

//AudioAttributes
public AudioAttributes build(){
    //
    String mCurName = ActivityThread.currentPackageName();
    if(mCurName != null && mCurName.equals("com.android.nav")){
        //重置媒体ContentType
        mContentType = AudioManager.STREAM_ALARM;
        //重置mUsage 类型
        mUsage = USAGE_ALARM;
    }
    //
}