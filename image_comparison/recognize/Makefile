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
	@pvisu ../data/lena-color.ppm ; read

	@pvisu ../data/lena-color.ppm ; read
	@./recognize ../data/lena-color.ppm ../data/lena-color.ppm;

	@./do_transformation ../data/lena-color.ppm reverse.ppm reverse ; pvisu reverse.ppm ; read
	@./recognize ../data/lena-color.ppm reverse.ppm;

	@./do_transformation ../data/lena-color.ppm grey.ppm grey ; pvisu grey.ppm ; read
	@./recognize ../data/lena-color.ppm grey.ppm;

	@./do_transformation grey.ppm grey_reverse.ppm reverse ; pvisu grey_reverse.ppm ; read
	@./recognize ../data/lena-color.ppm grey_reverse.ppm;

	@./do_transformation ../data/lena-color.ppm crop_1.ppm crop 100 200 250 200 ; pvisu crop_1.ppm ; read
	@./recognize ../data/lena-color.ppm crop_1.ppm;

	@./do_transformation ../data/lena-color.ppm crop_2.ppm crop 200 100 200 200 ; pvisu crop_2.ppm ; read
	@./recognize crop_2.ppm ../data/lena-color.ppm;

	@./do_transformation crop_1.ppm reverse_crop.ppm reverse ; pvisu reverse_crop.ppm ; read
	@./recognize ../data/lena-color.ppm reverse_crop.ppm;

	@./do_transformation grey.ppm crop_grey.ppm crop 100 200 250 200 ; pvisu crop_grey.ppm ; read
	@./recognize ../data/lena-color.ppm crop_grey.ppm;

	@./do_transformation crop_grey.ppm crop_grey_reverse.ppm reverse ; pvisu crop_grey_reverse.ppm ; read
	@./recognize ../data/lena-color.ppm crop_grey_reverse.ppm;

	@./do_transformation ../data/lena-color.ppm resize.ppm resize 1000 1000 ; pvisu resize.ppm ; read
	@./recognize ../data/lena-color.ppm resize.ppm;

	@./do_transformation reverse.ppm resize_reverse.ppm resize 250 250 ; pvisu resize_reverse.ppm ; read
	@./recognize ../data/lena-color.ppm resize_reverse.ppm;

	@./do_transformation grey.ppm resize_grey.ppm resize 512 230 ; pvisu resize_grey.ppm ; read
	@./recognize ../data/lena-color.ppm resize_grey.ppm;

	@./do_transformation grey_reverse.ppm resize_grey_reverse.ppm resize 230 512 ; pvisu resize_grey_reverse.ppm ; read
	@./recognize ../data/lena-color.ppm resize_grey_reverse.ppm;

	@pvisu ../data/pepper.ppm ; read
	@./recognize ../data/lena-color.ppm ../data/pepper.ppm

	@rm -rf *.ppm
	@killall pvisu

clean:
	rm -f *.o

cleanall: clean
	rm -rf $(BIN) *.ppm *~

.PHONY :
	clean all
