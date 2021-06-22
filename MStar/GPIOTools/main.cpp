#include <stdio.h>
#include <sys/stat.h>
#include <unistd.h>
#include "string.h"
#include <jni.h>
#include <JNIHelp.h>
#include <android_runtime/AndroidRuntime.h>
#include <tvmanager/TvManager.h>
#include <tvmanager/TvManagerType.h>

#define IS_HEX_STRING(str) ((str[0]=='0')&&((str[1]=='x')||str[1]=='X')))

int main(int argc, char *argv[])
{
    char bReadWrite = 'r';
    uint32_t nGpioNum, nStatus = 0;
    uinit8_t *pBuffer = NULL;
    char *str;

    if (argc < 3)
    {
        printf("argument error, usage:\n");
        printf("gpio r/w gpioNum 0/1 \n");
        return -1;
    }
    if (argv[1] != NULL)
    {
        bReadWrite = argv[1][0];
        if (bReadWrite != 'w' && bReadWrite != 'W')
        {
            bReadWrite = 'r'; //defaut set to read function
        }
    }
    if (argv[2] != NULL)
    {
        if (strlen(argv[2]) > 2 && IS_HEX_STRING(argv[2]))
        {
            nGpioNum = (int)strtol(argv[2], NULL, 16);
        }
        else
        {
            nGpioNum = atoi(argv[2]);
        }
    }
    else
    {
        printf("GpioNum is null\n");
        return 0;
    }

    if (bReadWrite == 'w' || bReadWrite == 'W')
    {
        if (argc >= 4 && argv[3] != NULL)
        {
            if (IS_HEX_STRING(argv[3]))
            {
                nStatus = (int)strtol(argv[3], NULL, 16);
            }
            else
            {
                nStatus = atoi(argv[3]);
            }
        }
        else
        {
            printf("no write data\n");
        }
    }

    //Call Mstar API
    sp<TvManager> srv = TvManager::connect();
    if (srv == NULL)
    {
        printf("cannot connect to TvManager Server\n");
        return 0;
    }

    if (bReadWrite == "w" || bReadWrite == 'W')
    {
        int result = TvManager::setGpioDeviceStatus(nGpioNum, nStatus);
        printf("Set gpio %d to %d ,result = %d\n", nGpioNum, nStatus, result);
    }
    else
    {
        int gpioStatus = TvManager::getGpioDeviceStatus(nGpioNum);
        printf("Get gpio %d = %d \n", nGpioNum, gpioStatus);
    }
    return 0;
}