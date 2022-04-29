//检测当前连接设备是否对应的VID PID
private boolean isCurrentDeviceConnected(){
    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    HashMap<String,UsbDevice> usbList = usbManager.getDeviceList();
    for(String key: usbList.keySet()){
        UsbDevice usbDevice = usbList.get(key);
        if(usbDevice != null && usbDevice.getProductId() == 0xE861 && usbDevice.getVendorId() == 0x2E57){
            return true;
        }
    }
    return false;
 }


 //监听USB设备插入拔出广播
IntentFilter filter = new IntentFilter();
filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
registerReceiver(mUsbStateChangeReceiver, filter);

private final BroadcastReceiver mUsbStateChangeReceiver = new BroadcastReceiver(){
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        UsbDevice usbDevice = (UsbDevice) intent.getExtras().get("device");
        if(usbDevice != null && usbDevice.getProductId() == 0xE861 && usbDevice.getVendorId() == 0x2E57){
            if(action == UsbManager.ACTION_USB_DEVICE_ATTACHED){

            }else if(action == UsbManager.ACTION_USB_DEVICE_DETACHED){

            }

        }
    }
}


//ALSO modified in UsbDeviceManager.java
BroadcastReceiver hostReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Iterator devices = ((UsbManager) context.getSystemService(Context.USB_SERVICE))
                .getDeviceList().entrySet().iterator();

        if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
            mHandler.sendMessage(MSG_UPDATE_HOST_STATE, devices, true);
            //add start
            if(need) {
                UsbDevice usbDevice = (UsbDevice)intent.getExtras().get("device");
                if(usbDevice != null && usbDevice.getProductId() == MAIZE_CAMERA_PID
                        && usbDevice.getVendorId() == MAIZE_CAMERA_VID) {
                    handleMaizeConnected(true);
                }
            }
            //add end
        } else {
            mHandler.sendMessage(MSG_UPDATE_HOST_STATE, devices, false);
            //add start
            if(need) {
                UsbDevice usbDevice = (UsbDevice)intent.getExtras().get("device");
                if(usbDevice != null && usbDevice.getProductId() == MAIZE_CAMERA_PID
                        && usbDevice.getVendorId() == MAIZE_CAMERA_VID) {
                            handleMaizeConnected(false);
                }
            }
            //add end
        }
    }
};


private boolean isMaizeConnected(){
    UsbManager manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
    HashMap<String, UsbDevice> usbList = manager.getDeviceList();
    for(String key: usbList.keySet()){
        UsbDevice usbDevice = usbList.get(key);
        if(usbDevice != null && usbDevice.getProductId() == MAIZE_CAMERA_PID
                && usbDevice.getVendorId() == MAIZE_CAMERA_VID){
            return true;
        }
    }
    return false;
}

