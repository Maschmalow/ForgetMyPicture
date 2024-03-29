/**
 * \file do_transformation.c
 * \brief Create a destination image with one transformation from a source image. 
 * \author Pierre PLUMIER
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include <bcl.h>
#include "transformation.h"

#define ERROR_FILE "error.txt" // Path of the file to write the possible errors


/**
 * \fn void use (char * s, char * transformation)
 * \brief Print the argument error and exit the do_transformation program. 
 *
 * \param *s Name of the do_transformation program
 * \param *transformation Name of the transformation
 */
void use(char * s, char * transformation)
{
  FILE *file = fopen(ERROR_FILE, "a");

  if (!strcmp(transformation, "resize"))
    fprintf(file, "Use: %s <source> <destination> <transformation> <nb_cols> <nb_rows>\n", s);
  else
    if (!strcmp(transformation, "crop"))
      fprintf(file, "Use: %s <source> <destination> <transformation> <nb_cols> <nb_rows> <first_col> <first_row>\n", s);
    else
      fprintf(file, "Use: %s <source> <destination> <transformation>\n", s);

  fclose(file);
  exit(EXIT_FAILURE);
}


/**
 * \fn void process(char * ims, char * imd, char * transformation, int nb_cols, int nb_rows, int first_col, int first_row, char * s)
 * \brief Create a destination image with one transformation from a source image.  
 *
 * \param *ims Path of the source image
 * \param *imd Path of the destination image
 * \param *transformation Name of the transformation
 * \param nb_cols It is only used to crop or resize. Number of columns of the destination image.
 * \param nb_rows It is only used to crop or resize. Number of rows of the destination image.
 * \param first_col It is only used to crop. Column of the source image which is the first column of the destination image.
 * \param first_row It is only used to crop. Row of the source image which is the first row of the destination image.
 * \param *s Name of the do_transformation program
 */
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
	  FILE *file = fopen(ERROR_FILE, "a");
	  fprintf(file, "transformation not defined\n");
	  fclose(file);
	  pnm_free(finalImage);
	  pnm_free(originImage);
	  use(s, transformation);
	}

  pnm_save(finalImage, PnmRawPpm, imd);
  pnm_free(finalImage);
  pnm_free(originImage);
}


#define UNUSED_AND_NON_VALID_PARAMETER -1 // it is a parameter just to have the same number of arguments

/**
 * \fn int main(int argc, char *argv[])
 * \brief do_transformation program start.
 *
 * \param argc Number of arguments
 * \param *argv[] Array of arguments: argv[1] Path of the source image | argv[2] Path of the destination image | argv[3] Name of the transformation | argv[4] It is only used to crop or resize. Number of columns of the destination image. | argv[5] It is only used to crop or resize. Number of rows of the destination image. | argv[6] It is only used to crop. Column of the source image which is the first column of the destination image. | argv[7] It is only used to crop. Row of the source image which is the first row of the destination image.
 *
 * \return EXIT_SUCCESS - Normal do_transformation program end
 */
int main(int argc, char *argv[])
{
  if (!strcmp(argv[3], "resize"))
    {
      if (argc != 6)
	use(argv[0], argv[3]);

      process(argv[1], argv[2], argv[3], atoi(argv[4]), atoi(argv[5]), UNUSED_AND_NON_VALID_PARAMETER, UNUSED_AND_NON_VALID_PARAMETER, argv[0]);
    }
  else
    if (!strcmp(argv[3], "crop"))
      {
	if (argc != 8)
	  use(argv[0], argv[3]);

	process(argv[1], argv[2], argv[3], atoi(argv[4]), atoi(argv[5]), atoi(argv[6]), atoi(argv[7]), argv[0]);
      }
    else
      {
	if (argc != 4)
	  use(argv[0], argv[3]);

	process(argv[1], argv[2], argv[3], UNUSED_AND_NON_VALID_PARAMETER, UNUSED_AND_NON_VALID_PARAMETER, UNUSED_AND_NON_VALID_PARAMETER, UNUSED_AND_NON_VALID_PARAMETER, argv[0]);
      }
  return EXIT_SUCCESS;
 }
