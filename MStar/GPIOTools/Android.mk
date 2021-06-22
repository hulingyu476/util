LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := GPIOTools
ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_MODULE)
LOCAL_STRIP_MODULE :=false
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := main.cpp

LOCAL_C_INCLUDES := \
    $(TARGET_OUT_HEADERS) \
    $(TARGET_UTOPIA_LIBS_DIR)/include \
    external/iniparser \
    device/mstar/common/libraries/mutils \
    $(JNI_H_INCLUDE) \
    frameworks/base/core/jni \
    $(TARGET_TVAPI_LIBS_DIR)/include \
    $(TARGET_TVAPI_LIBS_DIR)/../msrv/common/inc \
    $(TARGET_TVAPI_LIBS_DIR)/../../core/muf/tvos/include \
    $(TARGET_TVAPI_LIBS_DIR)/../../develop/core/muf/tvos/include

LOCAL_CFLAGS := -DMSOS_TYPE_LINUX

LOCAL_SHARED_LIBRARIES := \
    liblog \
    libutopia \
    libmutils \
    libiniparser \
    libandroid_runtime \
    libnativehelper \
    libcutils \
    libutils \
    libbinder \
    libtvmanager

include $(BUILD_EXECUTABLE)