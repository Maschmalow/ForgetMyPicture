ROOT=../bcl

CC=gcc
CFLAGS= -Wall -Wextra -std=c99 -I$(ROOT)/include
LDFLAGS= -L$(ROOT)/lib -lbcl -lm

BIN=recognize\
	server_recognize\
	do_transformation

all: $(BIN)

recognize: recognize.c transformation.o
	$(CC) $(CFLAGS) -DTEST_OR_SERVER=0 recognize.c transformation.o $(LDFLAGS) -o recognize

server_recognize: recognize.c transformation.o
	$(CC) $(CFLAGS) -DTEST_OR_SERVER=1 recognize.c transformation.o $(LDFLAGS) -o server_recognize

do_transformation: do_transformation.o transformation.o
	$(CC) do_transformation.o transformation.o $(LDFLAGS) -o do_transformation

do_transformation.o: do_transformation.c

transformation.o: transformation.h transformation.c

error: recognize
# test if there is an error (the number of arguments is not respected)
	@rm -rf error.txt
	@echo
	@./recognize ../data/lena-color.ppm ../data/lena-color.ppm error.txt one_more_argument
	@echo Now the content of the error file is\:
	@cat error.txt
	@echo
	@./recognize ../data/lena-color.ppm ../data/lena-color.ppm error.txt one_more_argument a_second_one_more_argument
	@echo Now the content of the error file is\:
	@cat error.txt
	@echo
	@./recognize ../data/lena-color.ppm ../data/lena-color.ppm # This case is accepted, there is not just the path of the error file.
	@echo Now the content of the error file is\:
	@cat error.txt
	@echo
	@./recognize ../data/lena-color.ppm
	@echo Now the content of the error file is\:
	@cat error.txt
	@echo
	@./recognize
	@echo Now the content of the error file is\:
	@cat error.txt

test_image_comparison: all
# image_comparison is tested twice: one for the test with sentence results and one for the server with number results (100 = recognized, 0 = NOT recognized)
# test if the original image is recognized with the same original image
	@echo
	@pvisu ../data/lena-color.ppm
	@pvisu ../data/lena-color.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm ../data/lena-color.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm ../data/lena-color.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if the reversed image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation ../data/lena-color.ppm reverse.ppm reverse ; pvisu reverse.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm reverse.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm reverse.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if the black and white image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation ../data/lena-color.ppm grey.ppm grey ; pvisu grey.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm grey.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm grey.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if the black and white reversed image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation grey.ppm grey_reverse.ppm reverse ; pvisu grey_reverse.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm grey_reverse.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm grey_reverse.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a cropped image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation ../data/lena-color.ppm crop_1.ppm crop 100 200 250 200 ; pvisu crop_1.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm crop_1.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm crop_1.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if the original image is recognized with a cropped image
	@pvisu ../data/lena-color.ppm
	@./do_transformation ../data/lena-color.ppm crop_2.ppm crop 200 100 200 200 ; pvisu crop_2.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize crop_2.ppm ../data/lena-color.ppm error.txt
	@echo For the server:
	@./server_recognize crop_2.ppm ../data/lena-color.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a reversed cropped image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation crop_1.ppm reverse_crop.ppm reverse ; pvisu reverse_crop.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm reverse_crop.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm reverse_crop.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a black and white cropped image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation grey.ppm crop_grey.ppm crop 100 200 250 200 ; pvisu crop_grey.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm crop_grey.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm crop_grey.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a black and white reversed cropped image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation crop_grey.ppm crop_grey_reverse.ppm reverse ; pvisu crop_grey_reverse.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm crop_grey_reverse.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm crop_grey_reverse.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a resized image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation ../data/lena-color.ppm resize.ppm resize 1000 1000 ; pvisu resize.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm resize.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm resize.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a reversed resized image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation reverse.ppm resize_reverse.ppm resize 250 250 ; pvisu resize_reverse.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm resize_reverse.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm resize_reverse.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a black and white resized image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation grey.ppm resize_grey.ppm resize 512 230 ; pvisu resize_grey.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm resize_grey.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm resize_grey.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a black and white reversed resized image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation grey_reverse.ppm resize_grey_reverse.ppm resize 230 512 ; pvisu resize_grey_reverse.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm resize_grey_reverse.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm resize_grey_reverse.ppm error.txt
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a different image is NOT recognized with the original image
	@pvisu ../data/lena-color.ppm
	@pvisu ../data/pepper.ppm
	@echo Image comparison result:
	@echo For the test:
	@./recognize ../data/lena-color.ppm ../data/pepper.ppm error.txt
	@echo For the server:
	@./server_recognize ../data/lena-color.ppm ../data/pepper.ppm error.txt
	@echo Press \"Enter\" to end\. ; read sth
	@killall pvisu

	@rm -rf *.ppm

clean:
	rm -f *.o

cleanall: clean
	rm -rf $(BIN) *.ppm *~ error.txt

.PHONY :
	clean all
