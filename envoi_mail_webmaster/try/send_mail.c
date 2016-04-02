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

void send_mail(char * admin_email_adr, char * client_email_adr/*, char * first_name, char * name*/)
{
  char * chaine, * chaine_bis;

  system("MAIL FROM:FMP@enseirb-matmeca.fr");
  asprintf(&chaine, "RCPT TO:%s", admin_email_adr);
  asprintf(&chaine_bis, "RCPT TO:%s", client_email_adr);
  system(chaine_bis);
  system(chaine);
  free(chaine);
  free(chaine_bis);
  asprintf(&chaine, "To:%s", admin_email_adr);
  asprintf(&chaine_bis, "Cc:%s", client_email_adr);
  system("DATA");
  system(chaine);
  system(chaine_bis);
  system("Subject:Detrimental photo problem");
  system("Dear Sir or Madam,");
  system("");
  system("");
  system("On your Internet domain, I have found a detrimental photo of me, which breaches my rights and my image. I would like to draw your attention to this situation.");
  system("");
  system("Indeed, under Article 16 (ex Article 286 TEC) Title II of the Treaty on European Union and the Treaty on the Functioning of the European Union, and the Data Protection Act, I request to delete the photo [ photo URL ] of your Internet domain.");
  system("");
  system("By two months, you have to respond to my request. Please contact me at the following email address [ user’s email address ].");
  system("");
  system("I should like also to remind you that, under the Data Protection Act, if an individual suffers damage, they are entitled to claim compensation from you.");
  system("");
  system("In the absence of action from you, I see myself compelled to take legal action by exercising later my right to object.");
  system("");
  system("Thanking you in advance for your diligence,");
  system("");
  system("Yours faithfully,");
  system("");
  system("[ First name NAME ]");
  system(".");
  free(chaine);
  free(chaine_bis);
}


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
      system("telnet smtp.enseirb-matmeca.fr 25");
      system("HELO enseirb.fr");

      for (j = 0; j < i && j < NB_ADR; j++)
	if (strcmp(adr[j], ""))
	  //send_mail(adr[j], argv[2]);

      system("QUIT");
    }
  else
    {
      for (j = 0; j < i && j < NB_ADR; j++)
	if (strcmp(adr[j], ""))
	  printf("%s\n", adr[j]);
    }
 
  return EXIT_SUCCESS; 
}
