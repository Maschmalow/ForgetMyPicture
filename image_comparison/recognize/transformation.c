#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#include <bcl.h>
#include "transformation.h"


void crop(const pnm * originImage, pnm * finalImage, int first_row, int first_col, int nb_rows, int nb_cols)
{
  int k, l, m;

  for(m = first_col; m < first_col + nb_cols; m++)
    for(l = first_row; l < first_row + nb_rows; l++)
      for(k = 0; k < 3; k++)
        pnm_set_component(*finalImage, l - first_row, m - first_col, k, pnm_get_component(*originImage, l, m, k));
}


void resize(const pnm * originImage, pnm * finalImage, int new_nb_cols, int new_nb_rows, int cols, int rows)
{  
  int m, l, k;

  for(m = 1; m < new_nb_cols; m++)
    for(l = 1; l < new_nb_rows; l++)
      for(k = 0; k < 3; k++)
	{
	  float x = (float)m * ((float)cols / (float)new_nb_cols);
	  float y = (float)l * ((float)rows / (float)new_nb_rows);
	  int i = (int)y;
	  int j = (int)x;
	  float dx = x - j;
	  float dy = y - i;
	  unsigned short couleur;

	  if (j == cols - 1 && i == rows - 1) // j + 1 and i + 1 are out of the picture, but dx = dy = 0 => simplification of the bilinear interpolation formula
	    couleur = (unsigned short)(pnm_get_component(*originImage, i, j, k));
	  else
	    if (j == cols - 1) // j + 1 is out of the picture, but dx = 0 => simplification of the bilinear interpolation formula
	      couleur = (unsigned short)((1-dy)*pnm_get_component(*originImage, i, j, k) 
					 + dy*pnm_get_component(*originImage, i + 1, j, k));
	    else
	      if (i == rows - 1) // i + 1 is out of the picture, but dy = 0 => simplification of the bilinear interpolation formula
	        couleur = (unsigned short)((1-dx)*pnm_get_component(*originImage, i, j, k) 
					   + dx*pnm_get_component(*originImage, i, j + 1, k));
	      else // normal case
		couleur = (unsigned short)((1-dx)*(1-dy)*pnm_get_component(*originImage, i, j, k) 
					   + dx*(1-dy)*pnm_get_component(*originImage, i, j + 1, k) 
					   + (1-dx)*dy*pnm_get_component(*originImage, i + 1, j, k) 
					   + dx*dy*pnm_get_component(*originImage, i + 1, j + 1, k));
	  
	  pnm_set_component(*finalImage, l, m, k, couleur);
	}
}


void reverse(const pnm * originImage, pnm * finalImage, int cols, int rows)
{
  int l, m, k;

  for(m = 0; m < cols; m++)
    for(l = 0; l < rows; l++)
      for(k = 0; k < 3; k++)
	pnm_set_component(*finalImage, l, cols - 1 - m, k, pnm_get_component(*originImage, l, m, k));
}


void grey(const pnm * originImage, pnm * finalImage, int cols, int rows)
{
  int k, l, m;

  for(m = 0; m < cols; m++){
    for(l = 0; l < rows; l++){
      unsigned short couleur = (pnm_get_component(*originImage, l, m, 0) +
				pnm_get_component(*originImage, l, m, 1) +
				pnm_get_component(*originImage, l, m, 2)) / 3;
      for(k = 0; k < 3; k++){
        pnm_set_component(*finalImage, l, m, k, couleur);
      }
    }
  }
}
