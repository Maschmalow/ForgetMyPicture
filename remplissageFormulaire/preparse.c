#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define MAX_LEN 500 //attention il faudra mettre une limite du nombre de caracteres utilisables

int main(int argc, char ** argv)
{
  //debut lecture des paramètres mis dans une chaine de caracteres     
  ssize_t len_read=0, wrote = 0;
  char buf[MAX_LEN];
  int i, j;
  int k = 0;
  for (i = 1; i < argc; i++)
    {
      for (j = 0; j < strlen(argv[i]); j++)
	{
	  buf[k] = argv[i][j];
	  k++;
	}
      buf[k] = ' ';
      k++;
    }
  buf[k - 1] = '\0';
  len_read = (ssize_t) k;
  //fin lecture des paramètres mis dans une chaine de caracteres

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
