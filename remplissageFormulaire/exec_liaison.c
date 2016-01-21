#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define MAX_LEN 500 //attention il faudra mettre une limite du nombre de caracteres utilisables

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
  
  execlp("php parse.php", "'France'", "'Jean-Michel'", "'DUPOND'", "'JeanDUPOND@gmail.com'", "'ID.png'", "'02/02/2016'", "'je'", "'naime'", "'pas'", "'cette'", "'photo,'", "'oui'", "'cest'", "'une'", "'virgule!'", "'fin_explication'", "'http://premiereadereferencer.com'", "'http://deuxiemeadereferencer.fr'", (char *) NULL);
  return EXIT_FAILURE; //les fonctions exec ne retourne que si il y a une erreur
}
