#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#include <bcl.h>
#include "transformation.h"

#define RESIZE_PRECISION 25


int same_pixel(const pnm * big, const pnm * small, int bigCol, int bigRow, int smallCol, int smallRow)
{
  int k;
  
  for (k = 0; k < 3; k++)
    if (pnm_get_component(*big, bigRow, bigCol, k) != pnm_get_component(*small, smallRow, smallCol, k))
      return 0;

  return 1;
}


int is_cropped(const pnm * image1, const pnm * image2, int cols1, int rows1, int cols2, int rows2)
{
  pnm small, big;
  int smallCols, smallRows, bigCols, bigRows;
  
  if (cols1 >= cols2 && rows1 >= rows2) // One image is cropped. This image is smalller than the other one: this image has less rows and less columns than the other one.
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


void process(char *ims, char *imt)
{
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

  int reussite = -1;
  // These next "else" are used for improving execution time.
  if (is_identical(&image1, &image2, cols1, rows1, cols2, rows2))
    reussite = 0;
  else
    if (is_identical(&reverse1, &image2, cols1, rows1, cols2, rows2))
      reussite = 1;
    else
      if (is_identical(&grey1, &image2, cols1, rows1, cols2, rows2) ||
	  is_identical(&image1, &grey2, cols1, rows1, cols2, rows2))
	reussite = 2;
      else
	if (is_identical(&grey_reverse1, &image2, cols1, rows1, cols2, rows2) ||
	    is_identical(&image1, &grey_reverse2, cols1, rows1, cols2, rows2))
	  reussite = 3;
	else
	  if (is_cropped(&image1, &image2, cols1, rows1, cols2, rows2))
	    reussite = 4;
	  else
	    if (is_cropped(&reverse1, &image2, cols1, rows1, cols2, rows2))
	      reussite = 5;	
	    else
	      if (is_cropped(&grey1, &image2, cols1, rows1, cols2, rows2) ||
		  is_cropped(&image1, &grey2, cols1, rows1, cols2, rows2))
		reussite = 6;
	      else
		if (is_cropped(&grey_reverse1, &image2, cols1, rows1, cols2, rows2) ||
		    is_cropped(&image1, &grey_reverse2, cols1, rows1, cols2, rows2))
		  reussite = 7;
		else
		  if (is_resized(&image1, &image2, cols1, rows1, cols2, rows2) ||
		      is_resized(&image2, &image1, cols2, rows2, cols1, rows1))
		    reussite = 8;
		  else
		    if (is_resized(&reverse1, &image2, cols1, rows1, cols2, rows2) ||
			is_resized(&image2, &reverse1, cols2, rows2, cols1, rows1))
		      reussite = 9;
		    else
		      if (is_resized(&grey1, &image2, cols1, rows1, cols2, rows2) ||
			  is_resized(&image2, &grey1, cols2, rows2, cols1, rows1) ||
			  is_resized(&image1, &grey2, cols1, rows1, cols2, rows2) ||
			  is_resized(&grey2, &image1, cols2, rows2, cols1, rows1))
			reussite = 10;
		      else
			if (is_resized(&grey_reverse1, &image2, cols1, rows1, cols2, rows2) ||
			    is_resized(&image2, &grey_reverse1, cols2, rows2, cols1, rows1) ||
			    is_resized(&image1, &grey_reverse2, cols1, rows1, cols2, rows2) ||
			    is_resized(&grey_reverse2, &image1, cols2, rows2, cols1, rows1))
			  reussite = 11;

  switch(reussite)
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

  pnm_free(image1);
  pnm_free(image2);
  pnm_free(reverse1);
  pnm_free(reverse2);
  pnm_free(grey1);
  pnm_free(grey2);
  pnm_free(grey_reverse1);
  pnm_free(grey_reverse2);
}

void usage (char *s)
{
  fprintf(stderr, "Usage: %s <ims> <imt>\n", s);
  exit(EXIT_FAILURE);
}

#define param 2
int main(int argc, char *argv[])
{
  if (argc != param+1)
    usage(argv[0]);

  process(argv[1], argv[2]);
  return EXIT_SUCCESS;
}
