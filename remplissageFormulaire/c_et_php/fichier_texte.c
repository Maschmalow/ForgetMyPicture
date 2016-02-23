#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include "constante.h"

#define TEXTE "France Jean-Michel DUPOND JeanDUPOND@gmail.com ID.png 02/02/2016 je n'aime pas cette photo, oui c'est une virgule! exclamation et les accents é à è ù ê î ô û â ï enfin point. interrogation? caracteres speciaux commentaire\\ retourligne\n \\ \n // lepluscomplique \\n fin_explication http://premiereadereferencer.com http://deuxiemeadereferencer.fr FIN_TEXTE BORNE_SECU"
//a voir comment le texte sera donne, il sera code comme dans une chaine de caracteres dans un printf (par exemple '\n' interprete comme un retour à la ligne)
//FIN_TEXTE est une balise pour indiquer la fin du texte, et on rajoute BORNE_SECU car ce qui se passe c'est que du texte est rajoute à la fin de texte lors de l'appel systeme et on n'en veut pas de ce texte en plus

int main(int argc, char ** argv)
{
 
  char * buf = TEXTE;
  ssize_t len_read=strlen(buf), wrote = 0;

  //debut ecriture de la chaine de caracteres dans la sortie standard
  do
    {
      wrote = write(STDOUT_FILENO, buf+wrote, len_read-wrote);
      if(wrote == -1)
	{
	  perror("write");
	  return EXIT_FAILURE;
	}
    }
  while(wrote<len_read);
  //fin ecriture de la chaine de caracteres dans la sortie standard

  return EXIT_SUCCESS;
}
