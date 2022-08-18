cmake_minimum_required(VERSION 3.4.1)

set(INCLUDE_DIR . ../boat/ ../boat/include/)

add_library(security SHARED security.cpp)
include_directories(${INCLUDE_DIR})

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 -O2 -Wall -Werror")
target_link_libraries(security log)