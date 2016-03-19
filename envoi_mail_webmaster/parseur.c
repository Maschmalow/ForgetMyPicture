/**
 * \file parseur.c
 * \brief To test if the addresses are the good ones
 * \author Pierre PLUMIER
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define SIZE_ADR 256 // Maximum size of an e-mail address
#define NB_ADR 10 // Maximum number of addresses


/**
 * \fn int main(int argc, char **argv)
 * \brief parseur program start.
 *
 * \param argc Number of arguments
 * \param **argv Array of arguments
 *
 * \return EXIT_SUCCESS - Normal parseur program end
 */
int main(int argc, char ** argv)
{
  char *path = argv[1]; // The first argument (argv[1]) is the path of the file

  FILE *file = fopen(path, "r");
  char line[SIZE_ADR + 20];
  char adr[NB_ADR][SIZE_ADR];
  int i, j;
  fgets(line, SIZE_ADR*sizeof(char), file);

  for (i = 0;
       line[0] != '@' && line[1] != '!' && line[2] != '@' && line[3] != '!' && line[4] != '@' && line[5] != '!'
	 && line[6] != 'F' && line[7] != 'I' && line[8] != 'N'
	 && line[9] != '@' && line[10] != '!' && line[11] != '@' && line[12] != '!' && line[13] != '@' && line[14] != '!';
       i++)
    {
      if (i < NB_ADR)
	{
	  sscanf(line, "e-mail:\t%[^\n]\n", adr[i]);
	  fgets(line, SIZE_ADR*sizeof(char), file);
	}
      else
	fprintf(stderr, "address array too small\n");
    }

  fclose(file);
  
  for (j = 0; j < i && j < NB_ADR; j++)
    printf("%s\n", adr[j]);
 
  return EXIT_SUCCESS; 
}
