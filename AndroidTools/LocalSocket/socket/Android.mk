LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include $(SDK_DIR)/Android.def



LOCAL_MODULE := sys_order.bin

LOCAL_MODULE_TAGS := optional

LOCAL_CPPFLAGS += -fexceptions -fkeep-inline-functions
LOCAL_SRC_FILES := SystemOrder.cpp 


LOCAL_SHARED_LIBRARIES := liblog libcutils libutils
#LOCAL_32_BIT_ONLY := true
include $(BUILD_EXECUTABLE)

