/**
 * \file who_is.c
 * \brief Print website host administrator e-mail addresses thanks to the function whois
 * \author Pierre PLUMIER
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define SIZE_MAX 256 // Maximum size of a web site domain


void do_whois(char * domain_name, int need_free)
{
  char * chaine;
  if (strcmp(domain_name, ""))
    {
      asprintf(&chaine, "whois %s | grep @", domain_name);
      system(chaine);
      free(chaine);
      if (need_free)
	free(domain_name);
    }
}


/**
 * \fn int main(int argc, char **argv)
 * \brief who_is program start.
 *
 * \param argc Number of arguments
 * \param **argv Array of arguments : argv[1] Web address/URL
 *
 * \return EXIT_SUCCESS - Normal who_is program end
 */
int main(int argc, char ** argv)
{
  int i;
  int found = 0;
  int state = -2;
  char * domain_name = "", * domain_name_2 = "", domain_name_3[SIZE_MAX] = "", domain_name_4[SIZE_MAX] = "";
  char tmp[SIZE_MAX];
  
  for (i = 0; i < strlen(argv[1]) && !found; i++)
    {
      switch (state)
	{
	case -2:
	  if (argv[1][i] == '/' && argv[1][i + 1] == '/')
	    state = -1;
	  break;
	case -1:
	    state = 0;
	  break;
	case 0:
	  if (argv[1][i] == '.')
	    state = 2;
	  asprintf(&domain_name_2, "%c", argv[1][i]);
	  state = 1;
	  break;
	case 1:
	  if (argv[1][i] == '.')
	    {
	      strcpy(domain_name_4, domain_name_2);
	      state = 2;
	    }
	  strcpy(tmp, domain_name_2);
	  free(domain_name_2);
	  asprintf(&domain_name_2, "%s%c", tmp, argv[1][i]);
	  break;
 	case 2:
	  asprintf(&domain_name, "%c", argv[1][i]);
	  strcpy(tmp, domain_name_2);
	  free(domain_name_2);
	  asprintf(&domain_name_2, "%s%c", tmp, argv[1][i]);
	  state = 3;
	  break;
	case 3:
	  if (argv[1][i] == '.')
	    {
	      strcpy(domain_name_3, domain_name);
	      strcpy(domain_name_4, domain_name_2);
	    }

	  if (argv[1][i] == '/')
	    found = 1;
	  else
	    {
	      strcpy(tmp, domain_name);
	      free(domain_name);
	      asprintf(&domain_name, "%s%c", tmp, argv[1][i]);
	      strcpy(tmp, domain_name_2);
	      free(domain_name_2);
	      asprintf(&domain_name_2, "%s%c", tmp, argv[1][i]);
	    }
	  break;
	}
    }

  // Do the function whois with the web site domain
  do_whois(domain_name, 1);
  do_whois(domain_name_2, 1);
  do_whois(domain_name_3, 0);
  do_whois(domain_name_4, 0);

  printf("@!@!@!FIN@!@!@!\n");
  return EXIT_SUCCESS; 
}
