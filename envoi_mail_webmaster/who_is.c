/**
 * \file who_is.c
 * \brief Print website host administrator e-mail addresses thanks to the function whois
 * \author Pierre PLUMIER
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/**
 * \fn int main(int argc, char **argv)
 * \brief who_is program start.
 *
 * \param argc Number of arguments
 * \param **argv Array of arguments
 *
 * \return EXIT_SUCCESS - Normal who_is program end
 */
int main(int argc, char ** argv)
{
  char * chaine;
  asprintf(&chaine, "whois %s | grep e-mail", argv[1]);
  system(chaine);
  free(chaine);
  printf("@!@!@!FIN@!@!@!\n");
  return EXIT_SUCCESS; 
}
