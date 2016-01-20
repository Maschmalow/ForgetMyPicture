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
  
  //debut modification de la chaine de caracteres
  int nb_carac_en_plus = 0;
  char chaine[2];
  sprintf(chaine, "'");
  int i, j;
  for (i = 0; i < len_read; i++)
    {     
      if (buf[i + nb_carac_en_plus] == chaine[0])
	{
	  for (j = MAX_LEN - 1; j > i + nb_carac_en_plus + 4 ; j--)
	    buf[j] = buf[j - 4];

	  buf[i + nb_carac_en_plus] = chaine[0];
	  buf[i + nb_carac_en_plus + 1] = '"';
	  buf[i + nb_carac_en_plus + 2] = chaine[0];
	  buf[i + nb_carac_en_plus + 3] = '"';
	  buf[i + nb_carac_en_plus + 4] = chaine[0];
	  nb_carac_en_plus += 4;
	}
    }

  len_read +=  (ssize_t)nb_carac_en_plus;
  //fin modification de la chaine de caracteres

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
