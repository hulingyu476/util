# I2CTools

读取I2c命令接口，已验证

## 原理：

sp<TvManager> srv = TvManager::connect();

TvManager::setGpioDeviceStatus(nGpioNum, nStatus);

TvManager::getGpioDeviceStatus(nGpioNum);

# GPIOTools

读写GPIO接口，未验证

## 原理：

 sp<FactoryManager> factoryManager = FactoryManager::connect();

factoryManager->writeBytesToI2C(nI2cNum, nRegAddrCount, nRedAddr, nLength, pBuffer));

(factoryManager->readBytesFromI2C(nI2cNum, nRegAddrCount, nRedAddr, nLength, pBuffer));