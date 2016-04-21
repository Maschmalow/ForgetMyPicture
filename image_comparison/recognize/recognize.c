/**
 * \file recognize.c
 * \brief Determine if 2 images are the same after some basic transformations. Try to determine if both images come from the same photo.
 * \author Pierre PLUMIER
 */

#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#include <bcl.h>
#include "transformation.h"

#define RESIZE_PRECISION 25 // There are different methods to resize an image. So 2 images resized from the same image with a different method could have some pixels which are slightly different. Therefore RESIZE_PRECISION is used to accept a slight difference between both resized images. 

#ifndef TEST_OR_SERVER
#define TEST_OR_SERVER 0 // The recognize program prints a sentence for the test and a number (100 = recognized, 0 = NOT recognized) for the server.
// So this variable is used to know if it is for the test or for the server.
// This varaible is equal to 0 for the test and is not equal to 0 (for example 1) for the server. 
#endif

/**
 * \fn int same_pixel(const pnm * big, const pnm * small, int bigCol, int bigRow, int smallCol, int smallRow)
 * \brief Compare a pixel of an image to a pixel of another image.
 *
 * \param *big An Image
 * \param *small Another Image
 * \param bigCol The big image pixel column
 * \param bigRow The big image pixel row
 * \param smallCol The small image pixel column
 * \param smallRow The small image pixel row
 *
 * \return True (ie 1) if and only if the pixel row bigRow and column bigCol of the big image and the pixel row smallRow and column smallCol of the small image are the same
 */
int same_pixel(const pnm * big, const pnm * small, int bigCol, int bigRow, int smallCol, int smallRow)
{
  int k;
  
  for (k = 0; k < 3; k++)
    if (pnm_get_component(*big, bigRow, bigCol, k) != pnm_get_component(*small, smallRow, smallCol, k))
      return 0;

  return 1;
}


/**
 * \fn int is_cropped(const pnm * image1, const pnm * image2, int cols1, int rows1, int cols2, int rows2)
 * \brief Determine if one image is a cropped image of the other image.
 *
 * \param *image1 The first image
 * \param *image2 The second image
 * \param cols1 Number of columns of image1
 * \param rows1 Number of rows of image1
 * \param cols2 Number of columns of image2
 * \param rows2 Number of rows of image2
 *
 * \return True (ie 1) if and only if one image is a cropped image of the other image
 */
int is_cropped(const pnm * image1, const pnm * image2, int cols1, int rows1, int cols2, int rows2)
{
  pnm small, big;
  int smallCols, smallRows, bigCols, bigRows;
  
  // One image is cropped. This image is smalller than the other one: this image has less rows and less columns than the other one.
  // So the smallest image and the biggest image are determined.
  if (cols1 >= cols2 && rows1 >= rows2) 
    {
      small = *image2;
      smallCols = cols2;
      smallRows = rows2;
      big = *image1;
      bigCols = cols1;
      bigRows = rows1;
    }
  else
    {
      small = *image1;
      smallCols = cols1;
      smallRows = rows1;
      big = *image2;
      bigCols = cols2;
      bigRows = rows2;
    }

  int l, m;
  int best_matching = 0;
  int current_matching;

  // Determine if the smallest image is included in the biggest image
  for (m = 0; m < bigCols - smallCols; m++)
    for (l = 0; l < bigRows - smallRows; l++)
      {
	for (current_matching = 0;
	     current_matching < smallCols * smallRows &&
	       same_pixel(&big, &small,
			  m + current_matching - current_matching / smallCols * smallCols, l + current_matching / smallCols,
			  current_matching - current_matching / smallCols * smallCols, current_matching / smallCols);
	     current_matching++);

	if (current_matching > best_matching)
	  best_matching = current_matching;
      }

  return best_matching == smallCols * smallRows;
}


/**
 * \fn int is_identical(const pnm * image1, const pnm * image2, int cols1, int rows1, int cols2, int rows2)
 * \brief Determine if both images are the same.
 *
 * \param *image1 The first image
 * \param *image2 The second image
 * \param cols1 Number of columns of image1
 * \param rows1 Number of rows of image1
 * \param cols2 Number of columns of image2
 * \param rows2 Number of rows of image2
 *
 * \return True (ie 1) if and only if both images are the same
 */
int is_identical(const pnm * image1, const pnm * image2, int cols1, int rows1, int cols2, int rows2)
{
  if (cols1 != cols2 || rows1 != rows2)
    return 0;

  int l, m, k;

  for(m = 0; m < cols1; m++)
    for(l = 0; l < rows1; l++)
      for(k = 0; k < 3; k++)
	if (pnm_get_component(*image2, l, m, k) != pnm_get_component(*image1, l, m, k))
	  return 0;
	    
  return 1;
}


/**
 * \fn int is_resized(const pnm * image1, const pnm * image2, int cols1, int rows1, int cols2, int rows2)
 * \brief Determine if the resized image1 image (resize1) and image2 seem to be the same.
 *
 * \param *image1 The first image which is resized to have the same numbers of colums and rows than image2 
 * \param *image2 The second image
 * \param cols1 Number of columns of image1
 * \param rows1 Number of rows of image1
 * \param cols2 Number of columns of image2
 * \param rows2 Number of rows of image2
 *
 * \return True (ie 1) if and only if the resized image1 image (resize1) and image2 seem to be the same
 */
int is_resized(const pnm * image1, const pnm * image2, int cols1, int rows1, int cols2, int rows2)
{
  int k, l, m;

  int nb_similar_pixels = 0;
  pnm resize1 = pnm_new(cols2, rows2, PnmRawPpm);
  resize(image1, &resize1, cols2, rows2, cols1, rows1);

  for(m = 0; m < cols2; m++)
    for(l = 0; l < rows2; l++)
      for(k = 0; k < 3; k++)
	{
	  if (abs((int) (pnm_get_component(*image2, l, m, k) -
			 pnm_get_component(resize1, l, m, k))) <
	      RESIZE_PRECISION)
	    nb_similar_pixels++;
	}

  pnm_free(resize1);
  if (nb_similar_pixels == cols2 * rows2 * 3)
    return 1;
  return 0;
}


/**
 * \fn void process(char *ims, char *imt)
 * \brief Determine if these 2 images are the same after some basic transformations. Try to determine if both images come from the same photo.
 *
 * \param *ims Path of the first image
 * \param *imt Path of the second image
 */
void process(char *ims, char *imt)
{
  // Create all needed images.
  pnm image1 = pnm_load(ims);
  int cols1 = pnm_get_width(image1);
  int rows1 = pnm_get_height(image1);
  
  pnm image2 = pnm_load(imt);
  int cols2 = pnm_get_width(image2);
  int rows2 = pnm_get_height(image2);

  pnm reverse1 = pnm_new(cols1, rows1, PnmRawPpm);
  reverse(&image1, &reverse1, cols1, rows1);

  pnm reverse2 = pnm_new(cols2, rows2, PnmRawPpm);
  reverse(&image2, &reverse2, cols2, rows2);

  pnm grey1 = pnm_new(cols1, rows1, PnmRawPpm);
  grey(&image1, &grey1, cols1, rows1);

  pnm grey2 = pnm_new(cols2, rows2, PnmRawPpm);
  grey(&image2, &grey2, cols2, rows2);

  pnm grey_reverse1 = pnm_new(cols1, rows1, PnmRawPpm);
  grey(&reverse1, &grey_reverse1, cols1, rows1);

  pnm grey_reverse2 = pnm_new(cols2, rows2, PnmRawPpm);
  grey(&reverse2, &grey_reverse2, cols2, rows2);

  int result = -1;
  // These next "else" are used for improving execution time.
  // Determine if these 2 images are the same after some basic transformations. And determine which transformations are done to have one of the images from the other one.
  if (is_identical(&image1, &image2, cols1, rows1, cols2, rows2))
    result = 0;
  else
    if (is_identical(&reverse1, &image2, cols1, rows1, cols2, rows2))
      result = 1;
    else
      if (is_identical(&grey1, &image2, cols1, rows1, cols2, rows2) ||
	  is_identical(&image1, &grey2, cols1, rows1, cols2, rows2))
	result = 2;
      else
	if (is_identical(&grey_reverse1, &image2, cols1, rows1, cols2, rows2) ||
	    is_identical(&image1, &grey_reverse2, cols1, rows1, cols2, rows2))
	  result = 3;
	else
	  if (is_cropped(&image1, &image2, cols1, rows1, cols2, rows2))
	    result = 4;
	  else
	    if (is_cropped(&reverse1, &image2, cols1, rows1, cols2, rows2))
	      result = 5;
	    else
	      if (is_cropped(&grey1, &image2, cols1, rows1, cols2, rows2) ||
		  is_cropped(&image1, &grey2, cols1, rows1, cols2, rows2))
		result = 6;
	      else
		if (is_cropped(&grey_reverse1, &image2, cols1, rows1, cols2, rows2) ||
		    is_cropped(&image1, &grey_reverse2, cols1, rows1, cols2, rows2))
		  result = 7;
		else
		  if (is_resized(&image1, &image2, cols1, rows1, cols2, rows2) ||
		      is_resized(&image2, &image1, cols2, rows2, cols1, rows1))
		    result = 8;
		  else
		    if (is_resized(&reverse1, &image2, cols1, rows1, cols2, rows2) ||
			is_resized(&image2, &reverse1, cols2, rows2, cols1, rows1))
		      result = 9;
		    else
		      if (is_resized(&grey1, &image2, cols1, rows1, cols2, rows2) ||
			  is_resized(&image2, &grey1, cols2, rows2, cols1, rows1) ||
			  is_resized(&image1, &grey2, cols1, rows1, cols2, rows2) ||
			  is_resized(&grey2, &image1, cols2, rows2, cols1, rows1))
			result = 10;
		      else
			if (is_resized(&grey_reverse1, &image2, cols1, rows1, cols2, rows2) ||
			    is_resized(&image2, &grey_reverse1, cols2, rows2, cols1, rows1) ||
			    is_resized(&image1, &grey_reverse2, cols1, rows1, cols2, rows2) ||
			    is_resized(&grey_reverse2, &image1, cols2, rows2, cols1, rows1))
			  result = 11;

  // Print the result.
  if (TEST_OR_SERVER)
    { // If the recognize program runs for the server.
      if (result == -1)
	printf("0\n"); // If these 2 images are NOT the same after being resized, cropped or reversed, or with a black and white image and a colour image.
      else
	printf("100\n"); // If these 2 images are the same after being resized, cropped or reversed, and/or with a black and white image and a colour image.
    }
  else
    { // If the recognize program runs for the test.
      switch(result)
	{
	case 0:
	  printf("These 2 images are the same.\n");
	  break;
	case 1:
	  printf("These 2 images are the same after being reversed.\n");
	  break;
	case 2:
	  printf("These 2 images are the same, except that one is a black and white image and the other one is a colour image.\n");
	  break;
	case 3:
	  printf("These 2 images are the same after being reversed, except that one is a black and white image and the other one is a colour image.\n");
	  break;
	case 4:
	  printf("These 2 images are the same after being cropped.\n");
	  break;
	case 5:
	  printf("These 2 images are the same after being cropped and reversed.\n");
	  break;
	case 6:
	  printf("These 2 images are the same after being cropped, except that one is a black and white image and the other one is a colour image.\n");
	  break;
	case 7:
	  printf("These 2 images are the same after being cropped and reversed, except that one is a black and white image and the other one is a colour image.\n");
	  break;
	case 8:
	  printf("These 2 images are the same after being resized.\n");
	  break;
	case 9:
	  printf("These 2 images are the same after being resized and reversed.\n");
	  break;
	case 10:
	  printf("These 2 images are the same after being resized, except that one is a black and white image and the other one is a colour image.\n");
	  break;
	case 11:
	  printf("These 2 images are the same after being resized and reversed, except that one is a black and white image and the other one is a colour image.\n");
	  break;
	default:
	  printf("These 2 images are NOT the same after being resized, cropped or reversed, or with a black and white image and a colour image.\n");
	}
    }

  // Free the heap memory.
  pnm_free(image1);
  pnm_free(image2);
  pnm_free(reverse1);
  pnm_free(reverse2);
  pnm_free(grey1);
  pnm_free(grey2);
  pnm_free(grey_reverse1);
  pnm_free(grey_reverse2);
}


#define PARAM 3
#define ERROR_FILE "error.txt" // Default path of the file to write the possible errors

/**
 * \fn void use (char * s, char * path)
 * \brief Print the argument error and exit the recognize program. 
 *
 * \param *s Name of the recognize program
 * \param *path Path of the file to write the possible errors
 * \param argc Number of arguments
 */
void use(char * s, char * path, int argc)
{

  FILE *file = fopen(path, "a");
  fprintf(file, "There are %d arguments, but this program needs to have %d arguments.\nUse: %s <path of the first image> <path of the second image> <path of the error file>\n", argc - 1, PARAM, s);
  fclose(file);
  exit(EXIT_SUCCESS); //exit(EXIT_FAILURE);
}


/**
 * \fn int main(int argc, char *argv[])
 * \brief recognize program start.
 *
 * \param argc Number of arguments
 * \param *argv[] Array of arguments: argv[1] Path of the first image | argv[2] Path of the second image | argv[3] Path of the file to write the possible errors
 *
 * \return EXIT_SUCCESS - Normal recognize program end
 */
int main(int argc, char *argv[])
{
  if (argc != PARAM+1 && argc != PARAM) // If there is not just the path of the error file which is the last argument, it is not a problem.
    {
      if (argc > PARAM+1)
	use(argv[0], argv[3], argc);
      else
	use(argv[0], ERROR_FILE, argc);
    }

  process(argv[1], argv[2]);
  return EXIT_SUCCESS;
}
