#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <cutils/sockets.h>  
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <pthread.h>
#include <assert.h>
#include <linux/fb.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <unistd.h>
#include <fcntl.h>
#include <utils/Log.h>

#include <sys/wait.h>
#include <sys/un.h>



#ifdef LOG_TAG
#undef LOG_TAG
#endif

#define LOG_TAG "sys_order"

#define SERVER_NAME "sys_order"



int main(int argc, char *argv[]){		
		
    int connect_number = 2;
    int fdSocket = -1, new_fd = -1;
    int ret;
    struct sockaddr_un peeraddr;
    socklen_t socklen = sizeof (peeraddr);
    int numbytes ;
    char buff[256],buff_temp[256];
	    
		int status = -1;
    
    fdSocket = android_get_control_socket(SERVER_NAME);
    if (fdSocket < 0) {
    	ALOGE("start service error, exit!");
			exit(-1);
    }
    ret = listen(fdSocket, connect_number);    
        
    if (ret < 0) {
    	ALOGE("listen localcocket service error, exit!");
	    close(fdSocket);
      exit(-1);
    }
    
    
    for(;;){
    
	    ALOGE("wait client connect ...");
	    new_fd = accept(fdSocket, (struct sockaddr *) &peeraddr, &socklen);
	    if (new_fd < 0 ) {
					close(new_fd);
	    		close(fdSocket);
	        exit(-1);
	    }else
	    	ALOGD("client connect ok");
    
    	
	    for(;;){
	    	memset(buff, 0, sizeof(buff));
	    	numbytes = recv(new_fd,buff,sizeof(buff),0);  //receive msg bytes
	    	if(numbytes <= 0){
	    		ALOGE("Connect error or disconnect, wait reconnect!");
	    		close(new_fd);
	    		break;
	    	}

				
				if(numbytes > 1){
					ALOGE("get app msg: %s, numbytes: %d\n", buff, numbytes);
							
					strcpy(buff_temp, buff);
					status = system(buff_temp);
					if(status < 0){
					    ALOGE("cmd: %s\t error: %s", buff, strerror(errno)); // 这里务必要把errno信息输出或记入Log
					}else	if(WIFEXITED(status)){
					    printf("normal termination, exit status = %d\n", WEXITSTATUS(status)); //取得cmdstring执行结果
					}else if(WIFSIGNALED(status)){
					    printf("abnormal termination,signal number =%d\n", WTERMSIG(status)); //如果cmdstring被信号中断，取得信号值
					}else if(WIFSTOPPED(status)){
					    printf("process stopped, signal number =%d\n", WSTOPSIG(status)); //如果cmdstring被信号暂停执行，取得信号值
					}
						
					if(send(new_fd,buff,strlen(buff),0)==-1){
						ALOGE("Send msg back error, exit!");
						close(new_fd);
		    		close(fdSocket);
						exit(0);
					}

				}
			
			usleep(50);
    }
    
    close(new_fd);
  }
    close(fdSocket);

    return 0;
}


