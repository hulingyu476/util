private boolean checkPasswordToUnLock(){
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
        KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        Log.d(TAG, "hoo() checkPasswordToUnLock = " + keyguardManager.inKeyguardRestrictedInputMode());
        return keyguardManager.inKeyguardRestrictedInputMode();
    }
    return false;
}
