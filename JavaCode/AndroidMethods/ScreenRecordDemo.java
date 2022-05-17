//<user-permission android:name="adnroid.permission.RECORD_AUDIO">
//<user-permission android:name="adnroid.permission.WRITE_EXTERNAL_STORAGE">

mMediaProjectionManager = (MedieaPorjectionManger) getSystemService(MEDIA_PROJECTION_SERVICE);

Intent captureInternt = mMediaProjectionManager.createScreenCaptureIntent();
startActivityForResult(captureIntent, REQUEST_CODE);

protected void onActivityResult(int requestCode, int resultCode, Internt data){
    if(requestCode = REQUEST_CODE && resultCode == RESULT_CODE) {
        mMediaProjectionManager = mMediaProjectionManager.getMediaProjection(resultCode,data);
    }
}

public VirturalDisplay createVirtualDisplay(@NonNull String name, int width, int height, int dpi, int flags,
                @Nullable Surface surface, @Nullable VirtualDisplay.Callback callback, @Nullable Handler handler)

//about flag
//当没有内容显示时，允许将内容镜像到专用显示器上。
//VIRTURE_DISPLAY_FLAG_AUTO_MIRROR

//仅显示此屏幕的内容，不镜像显示其他屏幕的内容。
//VIRTURE_DISPLAY_FALG_OWN_CONNECT_ONLY

//创建演示文稿的屏幕。
//VIRTURE_DISPLAY_FALG_PRESENTATION

//创建公开的屏幕。(一般选这个)
//VIRTURE_DISPLAY_FALG_PUBLIC

//创建一个安全的屏幕
//VIRTURE_DISPLAY_FALG_SECURE

//录屏
private void initRecorder(){
    File file = new File(Environment.getExternalStrorageDirectory(),System.currentTimeMills().".mp4");
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
    mediaRecorder.setOutputFormat(MediaRecoder.OutputFormat.THREE_GPP);
    mediaRecorder.setOutputFile(file.getAbsolutePath());
    mediaRecorder.setVideoSize(width,height);
    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    mediaRecorder.setVideoEncodingBitRate(5*1024*1024);
    mediaRecorder.setVideoFrameRate(30);

    try {
        mediaRecorder.prepare();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
//mediaRecorder.start()


//录屏直播
mEncoder = MediaCodec.createrEncoderByType(MIME_TYPE);
mEncoder.config(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
mInputSurface = mEncoder.createInputSurface();//Surface to VirturlDispaly
mEncoder.start();
