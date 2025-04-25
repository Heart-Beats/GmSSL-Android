cmake_minimum_required(VERSION 3.22.1)

project("gmssljni")


# 设置 so 文件生成路径(必须在 add_library 之前设置才可生效)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI})

# 添加所有源文件，编译到动态库中
aux_source_directory(src SRC_LIST)
add_library(${CMAKE_PROJECT_NAME} SHARED ${SRC_LIST})


#将给定的目录添加到编译器用来搜索头文件的目录中（若有子目录也需手动添加）
target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/include)

find_library(log-lib log)

# 链接动态库
target_link_libraries(${CMAKE_PROJECT_NAME} ${log-lib})


#添加子项目的目录
add_subdirectory(gmssl_lib)
#链接子项目， gmssl 为子项目的名称
target_link_libraries(${CMAKE_PROJECT_NAME} gmssl)


# ******************** 项目中依赖其他动态库下方需要修改  **************************************
## 根据 ANDROID_ABI  设置链接的动态库路径
set(DEPENDENT_LIBRARY_PATH ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI})

if (NOT EXISTS "${DEPENDENT_LIBRARY_PATH}" OR NOT IS_DIRECTORY "${DEPENDENT_LIBRARY_PATH}")
    message("${DEPENDENT_LIBRARY_PATH} 不存在或不是目录，不去链接动态库")
    return()
endif ()


# 链接配置文件中的动态库
file(GLOB DEPENDENT_LIBS "${DEPENDENT_LIBRARY_PATH}/*.so")
target_link_libraries(${CMAKE_PROJECT_NAME} ${DEPENDENT_LIBS})

# 添加一个 POST_BUILD 命令，将依赖库复制到动态库输出目录
add_custom_command(
        TARGET ${CMAKE_PROJECT_NAME}
        POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy_directory
        ${DEPENDENT_LIBRARY_PATH} ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}
        COMMENT "将依赖库复制到输出目录"
)