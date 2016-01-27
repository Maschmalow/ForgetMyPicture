#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define MAX_LEN 20 //attention il faudra mettre une limite du nombre de caracteres utilisables

#define TEXTE "France Jean-Michel DUPOND JeanDUPOND@gmail.com ID.png 02/02/2016 je n'aime pas cette photo, oui c'est une virgule! exclamation et les accents é à è ù ê î ô û â ï enfin point. interrogation? caracteres speciaux commentaire\\ retourligne\n \\ \n // lepluscomplique \\n fin_explication http://premiereadereferencer.com http://deuxiemeadereferencer.fr FIN_TEXTE BORNE_SECU"
//a voir comment le texte sera donne, il sera code comme dans une chaine de caracteres dans un printf (par exemple '\n' interprete comme un retour à la ligne)
//FIN_TEXTE est une balise pour indiquer la fin du texte, et on rajoute BORNE_SECU car ce qui se passe c'est que du texte est rajoute à la fin de texte lors de l'appel systeme et on n'en veut pas de ce texte en plus

#define FILE_NAME "parse.php"


int agrandir_tableau(char * tab, int taille, int indice)
{
  char * new_tab;
  while (taille <= indice)
    {
      taille *= 2;
      printf("taille %d indice %d\n", taille, indice);
      new_tab = malloc(sizeof(char) * taille);
      strcpy(new_tab, tab);
      free(tab);
      tab = new_tab;
    }
  return taille;
}  


void cpy(char * src, char * dst, int taille)
{
  int i;
  for (i = 0; i < taille; i++)
    dst[i] = src[i];
}


int main(int argc, char ** argv)
{
  int taille = MAX_LEN;
  char * buf = malloc (sizeof(char) * taille);
  taille = agrandir_tableau(buf, taille, strlen(TEXTE)); //il faut verifier pour ecrire cette chaine de carateres que l'on a bien le bon nombre de cases disponibles
  ssize_t len_read = strlen(TEXTE);
  cpy(TEXTE, buf, (int)len_read);
  int i, j;
  for (i = len_read; i < taille; i++)
    buf[i] = '\0';
  len_read++; // car il faut le premier caractere '\0' a la fin

  
  //debut modifications de la chaine de caracteres
  //debut modification pour apostrophes dans le texte de base
  int nb_carac_en_plus = 0;
  for (i = 0; i < len_read; i++)
    {
      printf("%d\n", i);
      taille = agrandir_tableau(buf, taille, len_read + nb_carac_en_plus); //pour ne pas taper a l'exterieur du tableau
      if (buf[i + nb_carac_en_plus] == '\'')
	{
	  taille = agrandir_tableau(buf, taille, len_read + nb_carac_en_plus + 4); //il faut verifier pour ne pas perdre de caracteres que l'on a bien 4 cases de plus disponibles avant de faire le decalage
	  for (j = taille - 1; j > i + nb_carac_en_plus + 4 ; j--)
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
  taille = agrandir_tableau(buf, taille, len_read + 1); //il faut verifier pour ne pas perdre de caracteres que l'on a bien 1 case de plus disponible avant de faire le decalage 
  for (j = taille - 1; j > 0 ; j--)
    buf[j] = buf[j - 1];

  buf[0] = '\'';
  len_read++;

  nb_carac_en_plus = 0;
  for (i = 0; i < len_read; i++)
    {
      taille = agrandir_tableau(buf, taille, len_read + nb_carac_en_plus); //pour ne pas taper a l'exterieur du tableau    
      if (buf[i + nb_carac_en_plus] == ' ')
	{
	  taille = agrandir_tableau(buf, taille, len_read + nb_carac_en_plus + 2); //il faut verifier pour ne pas perdre de caracteres que l'on a bien 2 cases de plus disponibles avant de faire le decalage
	  for (j = taille - 1; j > i + nb_carac_en_plus + 2 ; j--)
	    buf[j] = buf[j - 2];

	  buf[i + nb_carac_en_plus] = '\'';
	  buf[i + nb_carac_en_plus + 1] = ' ';
	  buf[i + nb_carac_en_plus + 2] = '\'';
	  nb_carac_en_plus += 2;
	}
    }

  len_read +=  (ssize_t)nb_carac_en_plus;

  taille = agrandir_tableau(buf, taille, len_read + 1); //il faut verifier pour ecrire ce caractere que l'on a bien 1 case de plus disponible
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
