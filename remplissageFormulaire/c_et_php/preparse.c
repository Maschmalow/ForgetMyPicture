#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include "constante.h"

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
  
  //debut modifications de la chaine de caracteres
  //debut modification pour apostrophes dans le texte de base
  int nb_carac_en_plus = 0;
  int i, j;
  for (i = 0; i < len_read; i++)
    {     
      if (buf[i + nb_carac_en_plus] == '\'')
	{
	  for (j = MAX_LEN - 1; j > i + nb_carac_en_plus + 4 ; j--)
	    buf[j] = buf[j - 4];

	  buf[i + nb_carac_en_plus] = '\'';
	  buf[i + nb_carac_en_plus + 1] = '"';
	  buf[i + nb_carac_en_plus + 2] = '\'';
	  buf[i + nb_carac_en_plus + 3] = '"';
	  buf[i + nb_carac_en_plus + 4] = '\'';
	  nb_carac_en_plus += 4;
	}
    }

  len_read +=  (ssize_t)nb_carac_en_plus;
  //fin modification pour apostrophes dans le texte de base

  //debut modification pour les apostrophes autour des mots
  for (j = MAX_LEN - 1; j > 0 ; j--)
    buf[j] = buf[j - 1];

  buf[0] = '\'';
  len_read++;

  nb_carac_en_plus = 0;
  for (i = 0; i < len_read; i++)
    {     
      if (buf[i + nb_carac_en_plus] == ' ')
	{
	  for (j = MAX_LEN - 1; j > i + nb_carac_en_plus + 2 ; j--)
	    buf[j] = buf[j - 2];

	  buf[i + nb_carac_en_plus] = '\'';
	  buf[i + nb_carac_en_plus + 1] = ' ';
	  buf[i + nb_carac_en_plus + 2] = '\'';
	  nb_carac_en_plus += 2;
	}
    }

  len_read +=  (ssize_t)nb_carac_en_plus;

  buf[(int)len_read] = '\'';
  len_read++;
  //fin modification pour les apostrophes autour des mots
  //fin modifications de la chaine de caracteres

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
