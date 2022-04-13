//To Avoid LMK kill the app, adjust you app adj to -1
//File: AcitivityManagerService.java
private final boolean applyOomAdjLocked(ProcessRecord app, boolean doingAll, long now,long nowElapsed) {
    boolean success = true;
    int changes = 0;

    if (app.curAdj != app.setAdj) {
        String[] packages = app.getPackageList();
        if(packages != null){
            for(String p : packages){
                if(p.equals("YourPackageName")){
                    //android.util.Log.d(TAG_OOM_ADJ, "set usettings adj -1");
                    app.curAdj = -1;
                    break;
                }
            }
        }
        ProcessList.setOomAdj(app.pid, app.info.uid, app.curAdj);
        if (DEBUG_SWITCH || DEBUG_OOM_ADJ) Slog.v(TAG_OOM_ADJ,
                "Set " + app.pid + " " + app.processName + " adj " + app.curAdj + ": " + app.adjType);
            app.setAdj = app.curAdj;
            app.verifiedAdj = ProcessList.INVALID_ADJ;
    }
｝