#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define MAX_LEN 500 //attention il faudra mettre une limite du nombre de caracteres utilisables

int main(int argc, char ** argv)
{     
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

  char * chaine = " Oui bien sur ;-)\n";
  int i;
  for (i = 0; i < strlen(chaine); i++)
    {
      buf[i + (int)len_read] = chaine[i];
    }

  len_read += strlen(chaine);
	
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

  return EXIT_SUCCESS;
}
