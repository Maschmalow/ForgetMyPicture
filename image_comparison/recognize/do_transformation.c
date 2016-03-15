#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include <bcl.h>
#include "transformation.h"


void usage (char *s, char * transformation)
{
  if (!strcmp(transformation, "resize"))
    fprintf(stderr, "Usage: %s <ims> <imd> <transformation> <nb_cols> <nb_rows>\n", s);
  else
    if (!strcmp(transformation, "crop"))
      fprintf(stderr, "Usage: %s <ims> <imd> <transformation> <nb_cols> <nb_rows> <first_col> <first_row>\n", s);
    else
      fprintf(stderr, "Usage: %s <ims> <imd> <transformation>\n", s);

  exit(EXIT_FAILURE);
}


void process(char * ims, char * imd, char * transformation, int nb_cols, int nb_rows, int first_col, int first_row, char * s)
{
  pnm originImage = pnm_load(ims);
  int cols = pnm_get_width(originImage);
  int rows = pnm_get_height(originImage);
  pnm finalImage;
  if (!strcmp(transformation, "crop") || !strcmp(transformation, "resize"))
    finalImage = pnm_new(nb_cols, nb_rows, PnmRawPpm);
  else
    finalImage = pnm_new(cols, rows, PnmRawPpm);

  if (!strcmp(transformation, "grey"))
    grey(&originImage, &finalImage, cols, rows);
  else
    if (!strcmp(transformation, "reverse"))
      reverse(&originImage, &finalImage, cols, rows);
    else
      if (!strcmp(transformation, "resize"))
	resize(&originImage, &finalImage, nb_cols, nb_rows, cols, rows);
      else
	if (!strcmp(transformation, "crop"))
	  crop(&originImage, &finalImage, first_row, first_col, nb_rows, nb_cols);
      else
	{
	  fprintf(stderr, "transformation not defined\n");
	  pnm_free(finalImage);
	  pnm_free(originImage);
	  usage(s, transformation);
	}

  pnm_save(finalImage, PnmRawPpm, imd);
  pnm_free(finalImage);
  pnm_free(originImage);
}


#define UNUSED_AND_NON_VALID_PARAMETER -1 // it is a parameter just to have the same number of arguments


int main(int argc, char *argv[])
{
  if (!strcmp(argv[3], "resize"))
    {
      if (argc != 6)
	usage(argv[0], argv[3]);

      process(argv[1], argv[2], argv[3], atoi(argv[4]), atoi(argv[5]), UNUSED_AND_NON_VALID_PARAMETER, UNUSED_AND_NON_VALID_PARAMETER, argv[0]);
    }
  else
    if (!strcmp(argv[3], "crop"))
      {
	if (argc != 8)
	  usage(argv[0], argv[3]);

	process(argv[1], argv[2], argv[3], atoi(argv[4]), atoi(argv[5]), atoi(argv[6]), atoi(argv[7]), argv[0]);
      }
    else
      {
	if (argc != 4)
	  usage(argv[0], argv[3]);

	process(argv[1], argv[2], argv[3], UNUSED_AND_NON_VALID_PARAMETER, UNUSED_AND_NON_VALID_PARAMETER, UNUSED_AND_NON_VALID_PARAMETER, UNUSED_AND_NON_VALID_PARAMETER, argv[0]);
      }
  return EXIT_SUCCESS;
 }
