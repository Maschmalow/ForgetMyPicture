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
 * \param **argv Array of arguments : argv[1] Path of the email address file
 *
 * \return EXIT_SUCCESS - Normal send_mail program end
 */
int main(int argc, char ** argv)
{
  char *path = argv[1];

  FILE *file = fopen(path, "r");
  char line[SIZE_ADR + 20];
  char adr[NB_ADR][SIZE_ADR];
  int i, j;
  fgets(line, SIZE_ADR*sizeof(char), file);

  for (i = 0;
       line[0] != '@' || line[1] != '!' || line[2] != '@' || line[3] != '!' || line[4] != '@' || line[5] != '!'
	 || line[6] != 'F' || line[7] != 'I' || line[8] != 'N'
	 || line[9] != '@' || line[10] != '!' || line[11] != '@' || line[12] != '!' || line[13] != '@' || line[14] != '!';
       i++)
    {
      if (i < NB_ADR)
	{
	  sscanf(line, "e-mail:\t%[^\n]\n", adr[i]);
	  sscanf(line, "Admin Email: %[^\n]\n", adr[i]);
	  sscanf(line, "Administrative Contact Email:\t%[^\n]\n", adr[i]);
	  fgets(line, SIZE_ADR*sizeof(char), file);
	}
      else
	fprintf(stderr, "address array too small\n");
    }

  fclose(file);
  
  if (TEST_OR_SEND)
    {
      for (j = 0; j < i && j < NB_ADR; j++)
	if (strcmp(adr[j], ""))
	  ;//void send_mail(char * admin_email_adr, char * client_email_adr/*, char * first_name, char * name*/)
    }
  else
    {
      for (j = 0; j < i && j < NB_ADR; j++)
	if (strcmp(adr[j], ""))
	  printf("%s\n", adr[j]);
    }
 
  return EXIT_SUCCESS; 
}
