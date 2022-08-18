cmake_minimum_required(VERSION 3.4.1)

# build libboat
set(INCLUDE_DIR include/)

add_library(boat SHARED boat.c boat_clipboard.c boat_event.c)
include_directories(${INCLUDE_DIR})

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 -O2 -Wall -Werror -DBUILD_BOAT")
target_link_libraries(boat dl android)
