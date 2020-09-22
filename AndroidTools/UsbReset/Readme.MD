```
1：编译main.c
2: 生成usbreset可执行文件

lsusb
Bus 003 Device 001: ID 1d6b:0002
Bus 002 Device 002: ID 1a40:0101
Bus 001 Device 001: ID 1d6b:0002
Bus 003 Device 013: ID 1a40:0101
Bus 001 Device 003: ID 28e1:b100
Bus 002 Device 006: ID 0781:5581
Bus 004 Device 001: ID 1d6b:0002
Bus 004 Device 007: ID 1a40:0101
Bus 002 Device 001: ID 1d6b:0002
Bus 003 Device 012: ID 1a40:0101

$ chmod +x usbreset
#./usbreset /dev/bus/usb/Bus/Device
./usbreset /dev/bus/usb/002/006




获取要重置的USB设备的总线和设备 ID:
$ lsusb
Bus 002 Device 003: ID 0fe9:9010 DVICO 
1
使编译后的程序成为可执行文件：

复制代码
$ chmod +x usbreset
使用sudo特权执行程序，在运行 lsusb 命令时，对 <Bus> 和 <Device> id进行必要的替换：

复制代码
$ sudo./usbreset/dev/bus/usb/002/003 

```

