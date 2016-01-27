#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include "constante.h"

//a voir comment le texte sera donne, il sera code comme dans une chaine de caracteres dans un printf (par exemple '\n' interprete comme un retour à la ligne)
//FIN_TEXTE est une balise pour indiquer la fin du texte, et on rajoute BORNE_SECU car ce qui se passe c'est que du texte est rajoute à la fin de texte lors de l'appel systeme et on n'en veut pas de ce texte en plus

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
  ssize_t len_read = 0;
  char texte[MAX_LEN];
  if((len_read = read(STDIN_FILENO,texte,MAX_LEN) ) == -1)
    {
      perror("read");
      return EXIT_FAILURE;
    }
	
  if(len_read ==0)/*EOF*/
    {
      return EXIT_SUCCESS;
    }
  //fin lecture de l'entree standard mis dans une chaine de caracteres

  char * buf;
  asprintf(&buf, "%s", texte);
  
  //debut modifications de la chaine de caracteres
  //debut modification pour apostrophes dans le texte de base
  int nb_carac_en_plus = 0;
  int i, j;
  for (i = 0; i < len_read; i++)
    {
      if (buf[i + nb_carac_en_plus] == '\'')
	{
	  asprintf(&buf, "%s1234", buf); //il faut que l'on ait 4 cases de plus disponibles avant de faire le decalage
	  for (j = strlen(buf) - 1; j > i + nb_carac_en_plus + 4 ; j--)
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
  asprintf(&buf, "%s1", buf); //il faut que l'on ait 1 case de plus disponible avant de faire le decalage
  for (j = strlen(buf) - 1; j > 0 ; j--)
    buf[j] = buf[j - 1];

  buf[0] = '\'';
  len_read++;

  nb_carac_en_plus = 0;
  for (i = 0; i < len_read; i++)
    {  
      if (buf[i + nb_carac_en_plus] == ' ')
	{
	  asprintf(&buf, "%s12", buf); //il faut que l'on ait 2 cases de plus disponibles avant de faire le decalage
	  for (j = strlen(buf) - 1; j > i + nb_carac_en_plus + 2 ; j--)
	    buf[j] = buf[j - 2];

	  buf[i + nb_carac_en_plus] = '\'';
	  buf[i + nb_carac_en_plus + 1] = ' ';
	  buf[i + nb_carac_en_plus + 2] = '\'';
	  nb_carac_en_plus += 2;
	}
    }

  len_read +=  (ssize_t)nb_carac_en_plus;

  asprintf(&buf, "%s1", buf); //il faut que l'on ait 1 case de plus disponible pour écrire ce caractere
  buf[(int)len_read] = '\'';
  len_read++;
  //fin modification pour les apostrophes autour des mots
  //fin modifications de la chaine de caracteres

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

  free(buf);
  free(chaine);
  return EXIT_SUCCESS;
}
