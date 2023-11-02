cmake_minimum_required(VERSION 3.2)
project(gmssljni)

# 设置 so 文件生成路径
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI})

#find_package(Java REQUIRED)
#find_package(JNI REQUIRED)
#include_directories(${JNI_INCLUDE_DIRS})
#include(UseJava)

#将给定的目录添加到编译器用来搜索头文件的目录中
include_directories(jni_include)
#include_directories(/usr/local/include)

aux_source_directory(. DIR_SRCS)

add_library(gmssljni SHARED ${DIR_SRCS})

#target_link_libraries(gmssljni ${JNI_LIBRARIES})
#target_link_libraries(gmssljni -L/usr/local/lib)

#为 gmssljni 添加需要链接的共享库
target_link_libraries(gmssljni gmssl)

# 指定编译给定目标时要使用的include目录
target_include_directories(gmssljni PUBLIC ${CMAKE_SOURCE_DIR}/src/include)

#enable_testing()

#向当前工程添加存放源文件的子目录
add_subdirectory(gmssl_lib)