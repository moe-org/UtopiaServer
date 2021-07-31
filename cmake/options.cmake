# * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
# * THIS FILE IS FROM MingMoe(me@kawayi.moe)                *
# * IS LICENSED UNDER MIT                                   *
# * File:     options.cmake                                 *
# * Content:  Cmake Module                                  *
# * Copyright (c) 2020-2021 MingMoe All rights reserved.    *
# * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

if(${CMAKE_BUILD_TYPE} STREQUAL "Debug")

    if(MSVC)
    #==========================================================
    #                   MSVC        DEBUG选项
    #==========================================================


    #==========================================================
    else() #            gcc/clang   DEBUG选项
    #==========================================================


    #==========================================================
    endif()
elseif(${CMAKE_BUILD_TYPE} STREQUAL "Release")
    if(MSVC)
    #==========================================================
    #                   MSVC        RELEASE选项
    #==========================================================



    #==========================================================
    else() #            gcc/clang   RELEASE选项
    #==========================================================



    #==========================================================
    endif() #           未知编译器  未知构建类型
    #==========================================================
else()
    message(FATAL_ERROR "Unknown CMAKE_BUILD_TYPE: ${CMAKE_BUILD_TYPE}")
endif()


if(MSVC)
#==========================================================
#                       MSVC        全局选项
#==========================================================

    # 开启C++20支持
    target_compile_options(${PROJECT_NAME} PRIVATE "/std:c++latest")


#==========================================================
else()  #               gcc/clang   全局选项
#==========================================================

    # 开启警告
    target_compile_options(${PROJECT_NAME} "-Wall")

    
    # 开启C++20支持
    if(CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
        if(CMAKE_CXX_COMPILER_VERSION VERSION_GREATER "11.0.0")
            target_compile_options(${PROJECT_NAME} PRIVATE "-std=c++20" )
        else()
            target_compile_options(${PROJECT_NAME} PRIVATE "-std=c++2a" )
        endif()
    else()
        message(FATAL_ERROR "NOT SUPPORT EXCEPT GCC")
    endif()



#==========================================================
endif()
