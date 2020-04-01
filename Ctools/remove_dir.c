#include <sys/stat.h>
#include <dirent.h>
#include <fcntl.h>

/**
 * 递归删除目录（删除该目录包含的文件和目录）
 * @dir：要删除的目录绝对路径
*/
 
 int remove_dir(const char *dir)
 {
     char cur_dir[] = ".";
     char up_dir[] = "..";
     char dir_name[128];
     DIR *dirp;
     struct dirent *dp;
     struct stat dir_stat;

    //  参数传递进来的目录不存在，则直接返回
    if ( 0 != access(dir,F_OK) ){
        return 0;
    }

    // 获取目录属性失败，返回错误
    if ( 0 > stat(dir, &dir_stat) ) {
        perror("get directory stat error");
        return -1;
    }
    
    if ( S_ISREG(dir_stat.st_mode) ) {
        remove(dir);
    }else if ( S_ISDIR(dir_stat.st_mode) ) {
        //目录文件，递归删除目录中内容
        dirp = opendir(dir0);
        while (( dp =readdir(dirp)) != NULL ) {
            //忽略.和..
            if ( ( 0 == strcmp(cur_dir,dp->d_name)) || ( 0 == strcmp(up_dir,dp->d_name)) ){
                continue;
            }

            sprintf(dir_name, "%s/%s", dir, dp->d_name);
            remove_dir(dir_name); //递归调用
        }
        closedir(dirp);

        rmdir(dir); //删除空目录
    }else{
        perror("unknow file type!");
    }

    return 0;
 }



