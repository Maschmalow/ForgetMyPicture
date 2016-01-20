#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define MAX_LEN 500 //attention il faudra mettre une limite du nombre de caracteres utilisables

#define TEXTE "France Jean-Michel DUPOND JeanDUPOND@gmail.com ID.png 02/02/2016 je n'aime pas cette photo, oui c'est une virgule! exclamation et les accents é à è ù ê î ô û â ï enfin point. interrogation? fin_explication http://premiereadereferencer.com http://deuxiemeadereferencer.fr"

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
