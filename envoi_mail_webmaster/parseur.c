#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define SIZE_ADR 256
#define NB_ADR 10

int main(int argc, char ** argv)
{
  char *path = argv[1]; //le nom de l'exemple est a passer en argument

  FILE *fichier = fopen(path, "r");
  char line[SIZE_ADR + 20];
  char adr[NB_ADR][SIZE_ADR];
  int i, j;
  fgets(line, SIZE_ADR*sizeof(char), fichier);

  for (i = 0; line[0] != 'F' || line[1] != 'I' || line[2] != 'N'; i++)
    {
      if (i < NB_ADR)
	{
	  sscanf(line, "e-mail:\t%[^\n]\n", adr[i]);
	  fgets(line, SIZE_ADR*sizeof(char), fichier);
	}
      else
	fprintf(stderr, "tableau d'adresse trop petit\n");
    }

  fclose(fichier);
  
  for (j = 0; j < i && j < NB_ADR; j++)
    printf("%s\n", adr[j]);
 
  return 0; 
}
