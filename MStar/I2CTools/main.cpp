#include <stdio.h>
#include <sys/stat.h>
#include <unistd.h>
#include "string.h"
#include <jni.h>
#include <JNIHelp.h>
#include <android_runtime/AndroidRuntime.h>
#include "factorymanager/FactoryManager.h"

#define IS_HEX_STRING(str) ((str[0] == '0') && ((str[1] == 'x') || (str[1] == 'X')))

int main(int argc, char *argv[])
{
    char bReadWrite = 'r';
    uint32_t nI2cNum, nDevAddr, nRegAddrCount;
    int nLength;
    uint8_t *nRedAddr = NULL;
    uint8_t *pBuffer = NULL;
    char *str;
    int nRegIndex = 0;

    //init
    nRegAddrCount = 1;
    nRegIndex = 1;

    //for (int m=0; m<argc; m++){
    //    printf("%d:%s\n", m+1, argv[m]);
    //}

    //read or write
    if (argc < 6)
    {
        printf("argument error, usage:\n");
        printf("i2ctool r/w i2cNum reglength regAddr length buffer\n");
        return -1;
    }

    if (argv[nRegIndex] != NULL)
    {
        bReadWrite = argv[nRegIndex][0];
        if (bReadWrite != 'w' && bReadWrite != 'W')
        {
            bReadWrite = 'r';
        }
    }
    nRegIndex++;

    //I2C Number
    if (argv[nRegIndex] != NULL)
    {
        if (strlen(argv[nRegIndex]) > 2 && IS_HEX_STRING(argv[nRegIndex]))
        {
            nI2cNum = (int)strtol(argv[nRegIndex], NULL, 16);
        }
        else
        {
            nI2cNum = atoi(argv[nRegIndex]);
        }
    }
    else
    {
        printf("i2cNum is null\n");
        return 0;
    }
    nRegIndex++;

    //I2C Reg length
    if (argv[nRegIndex] != NULL)
    {
        if (strlen(argv[nRegIndex]) > 2 && IS_HEX_STRING(argv[nRegIndex]))
        {
            nRegAddrCount = (int)strtol(argv[nRegIndex], NULL, 16);
        }
        else
        {
            nRegAddrCount = atoi(argv[nRegIndex]);
        }
    }
    else
    {
        printf("I2C Reg length is null\n");
        return 0;
    }
    nRegIndex++;

    //I2C Reg addr
    if (nRegAddrCount > 16)
    {
        printf("I2C RegAddrCount too long\n");
        return 0;
    }
    nRedAddr = (uint8_t *)malloc(nRegAddrCount);
    for (int j = 0; j < nRegAddrCount; j++)
    {
        if (argv[nRegIndex] != NULL)
        {
            if (strlen(argv[nRegIndex]) > 2 && IS_HEX_STRING(argv[nRegIndex]))
            {
                nRedAddr[j] = (int)strtol(argv[nRegIndex], NULL, 16);
            }
            else
            {
                nRedAddr[j] = (uint8_t)atoi(argv[nRegIndex]);
            }
        }
        else
        {
            printf("RedAddr is null\n");
            return 0;
        }
        nRegIndex++;
    }

    if (argv[nRegIndex] != NULL)
    {
        if (strlen(argv[nRegIndex]) > 2 && IS_HEX_STRING(argv[nRegIndex]))
        {
            nLength = (int)strtol(argv[nRegIndex], NULL, 16);
        }
        else
        {
            nLength = atoi(argv[nRegIndex]);
        }
    }
    else
    {
        printf("Length is null\n");
        return 0;
    }
    nRegIndex++;

    if (bReadWrite == 'w' || bReadWrite == 'W')
    {
        if (argc >= (6 + nRegAddrCount) && argv[nRegIndex] != NULL)
        {
            int i = 0;
            //int len = strlen(argv[5]);
            //int len = strlen(argv[4]);
            pBuffer = (uint8_t *)malloc(nLength + 1);
            if (pBuffer == NULL)
            {
                printf("malloc memory fail\n");
                return 0;
            }
            //printf("write data:%s\n",argv[nRegIndex]);
            /*
            if (nLength != (argc-6-nRegAddrCount))
            {
                printf("write date length error len:%d argc:%d\n",nLength,argc);
                return 0;
            }*/

            for (i = 0; i < nLength; i++)
            {
                //pBuffer[i] = argv[5][i]-0x30;
                if (strlen(argv[nRegIndex]) > 2 && IS_HEX_STRING(argv[nRegIndex]))
                {
                    pBuffer[i] = (uint8_t)strtol(argv[nRegIndex], NULL, 16);
                    ;
                }
                else
                {
                    pBuffer[i] = (uint8_t)atoi(argv[nRegIndex]);
                }
                //printf("pBuffer[%d]=0x%02x\n", i, pBuffer[i]);
                nRegIndex++;
            }
        }
        else
        {
            printf("no write data\n");
            return 0;
        }
    }

    //printf("r/w:%s\ni2cNum:%s\nregAddr:%s\nlength:%s\n",
    //	argv[1],argv[2],argv[3],argv[4]);

    //printf("r/w:%c\nni2cNum:%d\nnregAddr:%d\nnlength:%d\n",
    //	bReadWrite,nI2cNum,nRegAddrCount,nLength);
    //for (int n=0;n<nRegAddrCount;n++)
    //{
    //    printf("nRedAddr----%d:0x%x\n", n, nRedAddr[n]);
    //}

    //CALL MStar API
    sp<FactoryManager> factoryManager = FactoryManager::connect();
    if (factoryManager == NULL)
    {
        printf("can not connect to server");
        return 0;
    }

    if (bReadWrite == 'W' || bReadWrite == 'w')
    {
        for (int k = 0; k < nLength; k++)
        {
            printf("pBuffer----%d:0x%x\n", k, pBuffer[k]);
        }
        if (factoryManager->writeBytesToI2C(nI2cNum, nRegAddrCount, nRedAddr, nLength, pBuffer))
        {
            //printf("write success\n");
        }
        else
        {
            printf("write fail\n");
        }
    }
    else
    {
        pBuffer = (uint8_t *)malloc(nLength);
        if (pBuffer == NULL)
        {
            printf("malloc memory fail\n");
            return 0;
        }

        if (factoryManager->readBytesFromI2C(nI2cNum, nRegAddrCount, nRedAddr, nLength, pBuffer))
        {
            //printf("read success\n");

            for (int i = 0; i < nLength; i++)
            {
                printf("pBuffer[%d]:0x%02x\n", i, pBuffer[i]);
            }
        }
        else
        {
            printf("read fail\n");
        }
    }

    return 0;
}
