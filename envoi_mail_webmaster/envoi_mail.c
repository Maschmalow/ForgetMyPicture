#include <stdlib.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char ** argv)
{
  char * chaine;
  asprintf(&chaine, "whois %s | grep e-mail", argv[1]);
  system(chaine);
  free(chaine);
  return 0; 
}
