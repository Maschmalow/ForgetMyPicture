#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include "constante.h"

#define FILE_NAME "parse.php"


void cpy(char * src, char * dst, int taille)
{
  int i;
  for (i = 0; i < taille; i++)
    dst[i] = src[i];
}


int main(int argc, char ** argv)
{
  //debut lecture de l'entree standard mis dans une chaine de caracteres 
  ssize_t len_read=0, wrote = 0;
  char buf[MAX_LEN];
  if((len_read = read(STDIN_FILENO,buf,MAX_LEN) ) == -1)
    {
      perror("read");
      return EXIT_FAILURE;
    }
	
  if(len_read ==0)/*EOF*/
    {
      return EXIT_SUCCESS;
    }
  //fin lecture de l'entree standard mis dans une chaine de caracteres 

  //debut creation de la commande
  char entree[(int)len_read];
  cpy(buf, entree, (int)len_read);
  char * name = FILE_NAME;
  char * chaine = "php";
  asprintf(&chaine, "%s %s %s", chaine, name, entree);
  //printf("%s\n", chaine); //ca marche tres bien avec make tout, mais il y a qqs problemes avec make lien, c'est bizarre!!!
  //fin creation de la commande

  //debut envoi de la commande
  const char * chaine_system = (const char *)chaine;
  if(system(chaine_system) == -1)
    {
      perror("system");
      return EXIT_FAILURE;
    }
  //fin envoi de la commande

  free(chaine);
  return EXIT_SUCCESS;
}
