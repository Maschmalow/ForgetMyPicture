/**
 * \file who_is.c
 * \brief Print website host administrator e-mail addresses thanks to the function whois
 * \author Pierre PLUMIER
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define SIZE_MAX 256 // Maximum size of a web site domain

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
  char * domain_name = "";
  char tmp[SIZE_MAX];

  for (i = 0; i < strlen(argv[1]) && !found; i++)
    {
      strcpy(tmp, domain_name);

      if (i)
	free(domain_name);

      if (argv[1][i] != '.')
	asprintf(&domain_name, "%s%c", tmp, argv[1][i]);
      else
	{
	  if ((argv[1][i+1] == 'f' &&  argv[1][i+2] == 'r') ||
	      (argv[1][i+1] == 'c' &&  argv[1][i+2] == 'o' && argv[1][i+3] != 'm'))
	    {
	      asprintf(&domain_name, "%s.%c%c", tmp, argv[1][i+1], argv[1][i+2]);
	      found = 1;
	    }
	  else
	    {
	      if ((argv[1][i+1] == 'n' &&  argv[1][i+2] == 'e' &&  argv[1][i+3] == 't') ||
		  (argv[1][i+1] == 'o' &&  argv[1][i+2] == 'r' &&  argv[1][i+3] == 'g')||
		  (argv[1][i+1] == 'c' &&  argv[1][i+2] == 'o' &&  argv[1][i+3] == 'm'))
		{
		  asprintf(&domain_name, "%s.%c%c%c", tmp, argv[1][i+1], argv[1][i+2], argv[1][i+3]);
		  found = 1;
		}
	      else
	        asprintf(&domain_name, "");
	    }
	}
    }

  // Do the function whois with the web site domain
  char * chaine;
  asprintf(&chaine, "whois %s | grep e-mail", domain_name);
  system(chaine);
  free(chaine);
  asprintf(&chaine, "whois %s | grep Admin | grep Email", domain_name);
  system(chaine);
  free(chaine);
  free(domain_name);
  printf("@!@!@!FIN@!@!@!\n");
  return EXIT_SUCCESS; 
}
