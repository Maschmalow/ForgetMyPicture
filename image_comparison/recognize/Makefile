ROOT=../bcl

CC=gcc
CFLAGS= -Wall -Wextra -std=c99 -I$(ROOT)/include
LDFLAGS= -L$(ROOT)/lib -lbcl -lm

BIN=recognize\
	do_transformation

all: $(BIN)

recognize: recognize.o transformation.o
	$(CC) recognize.o transformation.o $(LDFLAGS) -o recognize

recognize.o: recognize.c

do_transformation: do_transformation.o transformation.o
	$(CC) do_transformation.o transformation.o $(LDFLAGS) -o do_transformation

do_transformation.o: do_transformation.c

transformation.o: transformation.h transformation.c

test: test_ppm

test_ppm: all
# test if the original image is recognized with the same original image
	@pvisu ../data/lena-color.ppm
	@pvisu ../data/lena-color.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm ../data/lena-color.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if the reversed image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation ../data/lena-color.ppm reverse.ppm reverse ; pvisu reverse.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm reverse.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if the black and white image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation ../data/lena-color.ppm grey.ppm grey ; pvisu grey.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm grey.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if the black and white reversed image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation grey.ppm grey_reverse.ppm reverse ; pvisu grey_reverse.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm grey_reverse.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a cropped image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation ../data/lena-color.ppm crop_1.ppm crop 100 200 250 200 ; pvisu crop_1.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm crop_1.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if the original image is recognized with a cropped image
	@pvisu ../data/lena-color.ppm
	@./do_transformation ../data/lena-color.ppm crop_2.ppm crop 200 100 200 200 ; pvisu crop_2.ppm
	@echo Image comparison result:
	@./recognize crop_2.ppm ../data/lena-color.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a reversed cropped image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation crop_1.ppm reverse_crop.ppm reverse ; pvisu reverse_crop.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm reverse_crop.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a black and white cropped image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation grey.ppm crop_grey.ppm crop 100 200 250 200 ; pvisu crop_grey.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm crop_grey.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a black and white reversed cropped image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation crop_grey.ppm crop_grey_reverse.ppm reverse ; pvisu crop_grey_reverse.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm crop_grey_reverse.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a resized image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation ../data/lena-color.ppm resize.ppm resize 1000 1000 ; pvisu resize.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm resize.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a reversed resized image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation reverse.ppm resize_reverse.ppm resize 250 250 ; pvisu resize_reverse.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm resize_reverse.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a black and white resized image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation grey.ppm resize_grey.ppm resize 512 230 ; pvisu resize_grey.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm resize_grey.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a black and white reversed resized image is recognized with the original image
	@pvisu ../data/lena-color.ppm
	@./do_transformation grey_reverse.ppm resize_grey_reverse.ppm resize 230 512 ; pvisu resize_grey_reverse.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm resize_grey_reverse.ppm;
	@echo Press \"Enter\" to do the next image comparison\. ; read sth
	@killall pvisu
# test if a different image is NOT recognized with the original image
	@pvisu ../data/lena-color.ppm
	@pvisu ../data/pepper.ppm
	@echo Image comparison result:
	@./recognize ../data/lena-color.ppm ../data/pepper.ppm
	@echo Press \"Enter\" to end\. ; read sth
	@killall pvisu

	@rm -rf *.ppm

clean:
	rm -f *.o

cleanall: clean
	rm -rf $(BIN) *.ppm *~

.PHONY :
	clean all