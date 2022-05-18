CMAKE_MINIMUM_REQUIRED(VERSION 3.1)
PROJECT(MyInstall VERSION 1.1)

#如果想生成静态库，使用下列语句
#ADD_LIBRARY(MyAdd STATIC MyAdd.cpp)
#TARGET_INCLUDE_DIRECTORIES(MyAdd PUBLIC ${CMAKE_SOURCE_DIR}/include)

#生成动态库，使用下列语句
ADD_LIBRARY(MyAdd SHARED MyAdd.cpp)
TARGET_INCLUDE_DIRECTORIES(MyAdd PRIVATE ${CMAKE_SOURCE_DIR}/include)
SET_TARGET_PROPERTIES(MyAdd PROPERTIES PUBLIC_HEADER ${CMAKE_SOURCE_DIR}/include/MyAdd.h)

#生产科执行文件
ADD_EXECUTABLE(MathCalc MathCalc.cpp)
TARGET_LINK_LIBRARIES(MathCalc MyAdd)
TARGET_INCLUDE_DIRECTORIES(MathCalc PRIVATE ${CMAKE_SOURCE_DIR}/include)

#Change install path； DESTINATION以下面为根目录
#Unix default: /usr/local
#Windows default: "C:/Program Files/${PROJECT_NAME}"
SET(CMAKE_INSTALL_PREFIX ${CMAKE_BINARY_DIR}/MyAdd)

#写入库的版本信息
#其中PACKAGE_VERSION便是我们在CMakeLists.txt开头project(MyInstall VERSION 1.1)中声明的版本号
INCLUDE(CMakePackageConfigHelpers)
write_basic_package_version_file(
    MathCalcConfigVersion.cmake
    VERSION ${PACKAGE_VERSION}
    COMPATIBILITY AnyNewerVersion
)

#将库文件，可执行文件，头文件安装到指定目录
install(TARGETS MyAdd MathCalc
        EXPORT MathCalcTargets
        LIBRARY DESTINATION lib
        ARCHIVE DESTINATION lib
        RUNTIME DESTINATION bin
        PUBLIC_HEADER DESTINATION include
    )

#将库的相关文件输出为MathCalcTargets.cmake，供find_package查找
#也就是上一步的EXPORT后面的名字
install(EXPORT MathCalcTargets
        FILE MathCalcTargets.cmake
        NAMESPACE MathCalc::
        DESTINATION lib/cmake/MathCalc
    )

#配置生成MyAddTarget.cmake文件，一并安装到lib/cmake/Myadd目录中
configure_file(MathCalcConfig.cmake.in MathCalcConfig.cmake @ONLY)
install(FILES "${CMAKE_CURRENT_BINARY_DIR}/MathCalcConfig.cmake"
                "${CMAKE_CURRENT_BINARY_DIR}/MathCalcConfigVersion.cmake"
        DESTINATION lib/cmake/MyAdd                
)
#in other project use this package 应用这个库
#find package(MathCalc 1.1)
#target_linked_library(otherapp MathCalc::MathCalc)