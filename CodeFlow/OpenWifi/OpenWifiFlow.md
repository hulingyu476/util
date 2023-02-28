# 说明
1. wifi打开流程
2. 基于Rockchip平台

---
# 调用ConnectivityManager中的startTethering方法
**调用方法：** ConnectivityManager中的startTethering方法
1. type： 
    - TETHERING_WIFI
    - TETHERING_USB
    - TETHERING_BLUETOOTH
2. showProvisionUi：
    - 一个帮助菜单，由厂家自己实现
3. callback: 
    - 执行打开wifi结果的回调
4. handler： 
    - 执行上面回调函数的线程
```java
public void startTethering(int type, boolean showProvisionUi, final OnStartheringCallback callback, Handler handler){
    Preconditions.checkNotNull(callback,"OnStarteringCallback cannot be null.");

    ResultReceiver wrappedCallback = new ResulutReceiver(handler){
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultCode == TETHER_ERROR_NO_ERROR){
                callback.onTetheringStarted();
            } else {
                callback.onTetheringFailed();
            }
        }
    };

    try {
        String pkgName = mContext.getOpPackageName();
        Log.i(TAG,"startTethering caller" + pkgName);
        mService.startTethering(type, wrappedCallback,showProvisionUi,pkgName);
    } catch (RemoteException e) {
        /Log.e(TAG,"Exception trying to start tethering.", e);
        wrappedCallback.send(TETHERING_ERROR_SERVICE_UNAVAIL,null);
    }
}
```

---
# ConnectivityService中的startTethering方法
1. 检查权限，
2. 判断是否支持热点，
3. 调用Tethering的startTethering
```java
@Override
public void startTethering(int type, ResultReceiver receiver, boolean showProvisionUi,String callerPkg) {
    ConnectivityManager.enforeTetheringChangePermission(mContext,callerPkg);
    if(!isTetheringSupported()) {
        receiver.send(ConnectivityManager.TETHER_ERROR_UNSUPPORTED,null);
        return;
    }
    mTethering.startTethering(type,receiver,showProvisionUi);
}
```
---

# startTethering方法
```java
public void startTethering(int type, ResultReceiver receiver, boolean showProvisionUi) {
    if (!isTetheringProvisioningRreequired()){
        enableTetheringInternal(type,true,receiver);
        return;
    }

    if (showProvisionUi){
        runUiTetherProvisionAndEnable(type,receiver);
    } else {
        runSilentTetherProvisionAndEnable(type,receiver);
    }
}
```
---
# enableTetheringInternal方法
```java
/**
* Enables or disables tethering for the given type. This should only be called once
* provisioning has succeeded or is not necessary. It will also schedule provisioning rechecks
* for the specified interface.
*/
private void enableTetheringInternal(int type, boolean enable, ResultReceiver receiver) {
    boolean isProvisioningRequired = enable && isTetherProvisioningRequired();
    int result;
    switch (type) {
        case TETHERING_WIFI:
            result = setWifiTethering(enable);
            if (isProvisioningRequired && result == TETHER_ERROR_NO_ERROR) {
                scheduleProvisioningRechecks(type);
            }
            sendTetherResult(receiver, result);
            break;
        case TETHERING_USB:
            result = setUsbTethering(enable);
            if (isProvisioningRequired && result == TETHER_ERROR_NO_ERROR) {
                scheduleProvisioningRechecks(type);
            }
            sendTetherResult(receiver, result);
            break;
        case TETHERING_BLUETOOTH:
            setBluetoothTethering(enable, receiver);
            break;
        default:
            Log.w(TAG, "Invalid tether type.");
            sendTetherResult(receiver, TETHER_ERROR_UNKNOWN_IFACE);
    }
}
```
---
# 主要看setWifiTethering方法
```java
private int setWifiTethering(final boolean enable) {
    int rval = TETHER_ERROR_MASTER_ERROR;
    final long ident = Binder.clearCallingIdentity();
    try {
        synchronized(mPublicSync) {
            mWifiTetherRequested = enable;
            final wifiManager mgr = getWifiManager();
            if ((enable && mgr.startSoftAp(null/*use exisiting wifi config*/)) ||
                (!enable && mgr.stopSoftAp())) {
                    rval = TETHERING_ERROR_NO_ERROR;
                }
        }
    } finally {
        Binder.restoreCallingIdentity(ident);
    }
    return rval;
}
```
---
# startSoftAP的实现在WifiiServiceImpl.java中
- 文件路径：frameworks/opt/net/wifi/service/java/com/android/server/wifi/WifiServiceImpl.java
```java
    /**
     * see {@link android.net.wifi.WifiManager#startSoftAp(WifiConfiguration)}
     * @param wifiConfig SSID, security and channel details as part of WifiConfiguration
     * @return {@code true} if softap start was triggered
     * @throws SecurityException if the caller does not have permission to start softap
     */
    @Override
    public boolean startSoftAp(WifiConfiguration wifiConfig) {
        // NETWORK_STACK is a signature only permission.
        enforceNetworkStackPermission();

        mLog.info("startSoftAp uid=%").c(Binder.getCallingUid()).flush();

        synchronized (mLocalOnlyHotspotRequests)    {
        // If a tethering request comes in while we have LOHS running (or requested), call stop
        // for softap mode and restart softap with the tethering config.
            if (!mLocalOnlyHotspotRequests.isEmpty()) {
                stopSoftApInternal();
            }
            return startSoftApInternal(wifiConfig, WifiManager.IFACE_IP_MODE_TETHERED);
        }
    }
     /**
     * Internal method to start softap mode. Callers of this method should have already checked
     * proper permissions beyond the NetworkStack permission.
     */
    private boolean startSoftApInternal(WifiConfiguration wifiConfig, int mode) {
        mLog.trace("startSoftApInternal uid=% mode=%")
                .c(Binder.getCallingUid()).c(mode).flush();

        // null wifiConfig is a meaningful input for CMD_SET_AP
        if (wifiConfig == null || WifiApConfigStore.validateApWifiConfiguration(wifiConfig)) {
            SoftApModeConfiguration softApConfig = new SoftApModeConfiguration(mode, wifiConfig);
            mWifiController.sendMessage(CMD_SET_AP, 1, 0, softApConfig);
            return true;
        }
        Slog.e(TAG, "Invalid WifiConfiguration");
        return false;
    }
```
---
# WifiController.java状态机
- 真正操作打开热点实在WifiStateMachinePrime.java中（各版本不同）
- 路径：frameworks/opt/net/wifi/service/java/com/android/server/wifi/WifiStateMachinePrime.java
```java
/**
* Method to enable soft ap for wifi hotspot.
*
* The supplied SoftApModeConfiguration includes the target softap WifiConfiguration (or null if
* the persisted config is to be used) and the target operating mode (ex,
* {@link WifiManager.IFACE_IP_MODE_TETHERED} {@link WifiManager.IFACE_IP_MODE_LOCAL_ONLY}).
*
* @param wifiConfig SoftApModeConfiguration for the hostapd softap
*/
public void enterSoftAPMode(@NonNull SoftApModeConfiguration wifiConfig) {
    mHandler.post(() -> {
        startSoftAp(wifiConfig);
    });
}
private void startSoftAp(SoftApModeConfiguration softapConfig) {
        Log.d(TAG, "Starting SoftApModeManager");

        WifiConfiguration config = softapConfig.getWifiConfiguration();
        if (config != null && config.SSID != null) {
            Log.d(TAG, "Passing config to SoftApManager! " + config);
        } else {
            config = null;
        }

        SoftApCallbackImpl callback = new SoftApCallbackImpl();
        ActiveModeManager manager = mWifiInjector.makeSoftApManager(callback, softapConfig);
        callback.setActiveModeManager(manager);
        manager.start();
        mActiveModeManagers.add(manager);
        updateBatteryStatsWifiState(true);
    }
```
---
# 找到SoftApManager.java的start()方法：
- 路径：frameworks/opt/net/wifi/service/java/com/android/server/wifi/SoftApManager.java
```java
    /**
     * Start soft AP with the supplied config.
     */
    public void start() {
        mStateMachine.sendMessage(SoftApStateMachine.CMD_START, mApConfig);
    }
  private class IdleState extends State {
            @Override
            public void enter() {
                mApInterfaceName = null;
                mIfaceIsUp = false;
            }

            @Override
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case CMD_START:
                        Log.e(TAG, "CMD_START.");
                        mApInterfaceName = mWifiNative.setupInterfaceForSoftApMode(
                                mWifiNativeInterfaceCallback);
                        if (TextUtils.isEmpty(mApInterfaceName)) {
                            Log.e(TAG, "setup failure when creating ap interface.");
                            updateApState(WifiManager.WIFI_AP_STATE_FAILED,
                                    WifiManager.WIFI_AP_STATE_DISABLED,
                                    WifiManager.SAP_START_FAILURE_GENERAL);
                            mWifiMetrics.incrementSoftApStartResult(
                                    false, WifiManager.SAP_START_FAILURE_GENERAL);
                            break;
                        }
                        updateApState(WifiManager.WIFI_AP_STATE_ENABLING,
                                WifiManager.WIFI_AP_STATE_DISABLED, 0);
                        int result = startSoftAp((WifiConfiguration) message.obj);
                        if (result != SUCCESS) {
                            int failureReason = WifiManager.SAP_START_FAILURE_GENERAL;
                            if (result == ERROR_NO_CHANNEL) {
                                failureReason = WifiManager.SAP_START_FAILURE_NO_CHANNEL;
                            }
                            updateApState(WifiManager.WIFI_AP_STATE_FAILED,
                                          WifiManager.WIFI_AP_STATE_ENABLING,
                                          failureReason);
                            stopSoftAp();
                            mWifiMetrics.incrementSoftApStartResult(false, failureReason);
                            break;
                        }
                        transitionTo(mStartedState);
                        break;
                    default:
                        // Ignore all other commands.
                        break;
                }

                return HANDLED;
            }
        }
```
---
# setupInterfaceForSoftApMode方法，此处就比较重要了，需要逐条分析：
路径：frameworks/opt/net/wifi/service/java/com/android/server/wifi/WifiNative.java
```java
/**
     * Setup an interface for Soft AP mode operations.
     *
     * This method configures an interface in AP mode in all the native daemons
     * (wificond, wpa_supplicant & vendor HAL).
     *
     * @param interfaceCallback Associated callback for notifying status changes for the iface.
     * @return Returns the name of the allocated interface, will be null on failure.
     */
    public String setupInterfaceForSoftApMode(@NonNull InterfaceCallback interfaceCallback) {
        Log.e(TAG,"setupInterfaceForSoftApMode");
        synchronized (mLock) {
            if (!startHal()) {
                Log.e(TAG, "Failed to start Hal");
                mWifiMetrics.incrementNumSetupSoftApInterfaceFailureDueToHal();
                return null;
            }
            Log.e(TAG, "liyang Hal start success");
            Iface iface = mIfaceMgr.allocateIface(Iface.IFACE_TYPE_AP);
            if (iface == null) {
                Log.e(TAG, "Failed to allocate new AP iface");
                return null;
            }
            iface.externalListener = interfaceCallback;
            iface.name = createApIface(iface);
            if (TextUtils.isEmpty(iface.name)) {
                Log.e(TAG, "Failed to create AP iface in vendor HAL");
                mIfaceMgr.removeIface(iface.id);
                mWifiMetrics.incrementNumSetupSoftApInterfaceFailureDueToHal();
                return null;
            }
            if (mWificondControl.setupInterfaceForSoftApMode(iface.name) == null) {
                Log.e(TAG, "Failed to setup iface in wificond on " + iface);
                teardownInterface(iface.name);
                mWifiMetrics.incrementNumSetupSoftApInterfaceFailureDueToWificond();
                return null;
            }
            iface.networkObserver = new NetworkObserverInternal(iface.id);
            if (!registerNetworkObserver(iface.networkObserver)) {
                Log.e(TAG, "Failed to register network observer on " + iface);
                teardownInterface(iface.name);
                return null;
            }
            // Just to avoid any race conditions with interface state change callbacks,
            // update the interface state before we exit.
            onInterfaceStateChanged(iface, isInterfaceUp(iface.name));
            Log.i(TAG, "Successfully setup " + iface);
            return iface.name;
        }
    }
```
---
# startHal()方法：
```java
/* Helper method invoked to start supplicant if there were no ifaces */
private boolean startHal() {
    synchronized （mLock) {
        if (!mIfaceMgr.hasAnyIface()) {
            if (mwifiVendorHal.isVendorHalSuppored()) {
                if (!mwifiVendorHal.startVendorHal()) {
                    Log.e(TAG，"Failed to start vendor HAL");
                    return false;
                }
            }else {
                Log.i(TAG,"Vendor Hal not supported, ignoring start.");
            }
        }
        return true;
    }
}
```