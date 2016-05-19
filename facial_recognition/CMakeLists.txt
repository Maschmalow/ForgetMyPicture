cmake_minimum_required(VERSION 2.8)
project( Opencv )
find_package( OpenCV REQUIRED )
add_executable( Recognizer Recognizer.cpp )

target_link_libraries( Recognizer ${OpenCV_LIBS} )
