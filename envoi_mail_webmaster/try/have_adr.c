/**
 * \file send_mail.c
 * \brief Send mail to website host administrators
 * \author Pierre PLUMIER
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define SIZE_ADR 256 // Maximum size of an e-mail address
#define NB_ADR 20 // Maximum number of addresses

#ifndef TEST_OR_SEND
#define TEST_OR_SEND 0
#endif


/**
 * \fn int main(int argc, char **argv)
 * \brief send_mail program start.
 *
 * \param argc Number of arguments
 * \param **argv Array of arguments: argv[1] Path of the email address file | argv[2] User's e-mail address | argv[3] User's First name | argv[4] User's LAST NAME | argv[5] Photo URL | argv[6] Citizenship
 *
 * \return EXIT_SUCCESS - Normal send_mail program end
 */
int main(int argc, char ** argv)
{
  char *path = argv[1];

  FILE *file = fopen(path, "r");
  char line[SIZE_ADR + 20];
  char adr[NB_ADR][SIZE_ADR];
  int i = 0, j, k;
  fgets(line, SIZE_ADR*sizeof(char), file);

  while (line[0] != '@' || line[1] != '!' || line[2] != '@' || line[3] != '!' || line[4] != '@' || line[5] != '!'
	 || line[6] != 'F' || line[7] != 'I' || line[8] != 'N'
	 || line[9] != '@' || line[10] != '!' || line[11] != '@' || line[12] != '!' || line[13] != '@' || line[14] != '!')
    {
      if (i < NB_ADR)
	{
	  if (sscanf(line, "e-mail:\t%[^\n]\n", adr[i]))
	    i++;
	  if (sscanf(line, "Admin Email: %[^\n]\n", adr[i]))
	    i++;
	  if (sscanf(line, "Administrative Contact Email:\t%[^\n]\n", adr[i]))
	    i++;
	  fgets(line, SIZE_ADR*sizeof(char), file);
	}
      else
	fprintf(stderr, "address array too small\n");
    }

  fclose(file);

  if (i)
    {
      char single_time_adr[i][SIZE_ADR];

      for (j = 0; j < i; j++)
	{
	  int single_time = 1;
	  for  (k = 0; k < j; k++)
	    if (!strcmp(adr[j], single_time_adr[k]))
	      single_time = 0;

	  if (single_time)
	    strcpy(single_time_adr[j], adr[j]);
	  else
	    strcpy(single_time_adr[j], "");
	}

      if (TEST_OR_SEND)
	{
	  for (j = 0; j < i; j++)
	    if (strcmp(single_time_adr[j], ""))
	      {
		char * chaine = "";
		asprintf(&chaine, "php send_mail.php %s %s %s %s %s %s", single_time_adr[j], argv[2], argv[3], argv[4], argv[5], argv[6]);
		system(chaine);
		free(chaine);
	      }
	}
      else
	{
	  for (j = 0; j < i; j++)
	    if (strcmp(single_time_adr[j], ""))
	      printf("%s\n", single_time_adr[j]);
	}

    }
 
  return EXIT_SUCCESS; 
}
