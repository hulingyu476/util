# Android LocalSocket Demo
实现一个Localsocket 用于app执行命令行脚本

1. socket文件夹编译出一个sys_order的可执行文件文件放到 /system/bin/sys_order

2. sys_order.te 是selinux te文件

3. device/hisilicon/bigfish/external/sepolicy/vendor/file_contexts 添加：

   /(vendor|system/vendor)/bin/scr_order u:object_r:scr_order_exec:s0

4. device\hisilicon\Hi3751V811\device_copyfile.mk 添加：

```
PRODUCT_COPY_FILES += \
	device/hisilicon/bigfish/prebuilts/scr_order:vendor/bin/scr_order
```

5. 文件scr_order放到device/hisilicon/bigfish/prebuilts/scr_order中

​      文件scr_order.te放到device/hisilicon/bigfish/external/sepolicy/vendor中

6. /device/hisilicon/bigfish/etc/init.bigfish.rc 修改：

```
service scr_record /vendor/bin/scr_order
       seclabel u:r:scr_order:s0
       class main                               
       socket scr_rec stream 666 system system  
       service pqserver /vendor/bin/sys_order
       class main
```

​     

7. java 实例代码





