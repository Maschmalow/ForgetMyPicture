#include <stdlib.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char ** argv) //permet de faire la commande whois pour un domaine
{
  char * chaine;
  asprintf(&chaine, "whois %s | grep e-mail", argv[1]);
  system(chaine);
  free(chaine);
  printf("@!@!@!FIN@!@!@!\n");
  return 0; 
}
