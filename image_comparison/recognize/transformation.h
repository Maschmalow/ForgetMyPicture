#ifndef DO_RESIZE_H
#define DO_RESIZE_H

void crop(const pnm * originImage, pnm * finalImage, int first_row, int first_col, int nb_rows, int nb_cols);

void resize(const pnm * originImage, pnm * finalImage, int new_nb_cols, int new_nb_rows, int cols, int rows);

void reverse(const pnm * originImage, pnm * finalImage, int cols, int rows);

void grey(const pnm * originImage, pnm * finalImage, int cols, int rows);

#endif
