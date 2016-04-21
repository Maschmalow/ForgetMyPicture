#ifndef TRANSFORMATION_H
#define TRANSFORMATION_H

/**
 * \file transformation.h
 * \brief Contain all transformation functions.
 * \author Pierre PLUMIER 
 */


/**
 * \fn void crop(const pnm * originImage, pnm * finalImage, int first_row, int first_col, int nb_rows, int nb_cols)
 * \brief Crop an image. The cropped image is the source image from (first_row) row and (first_col) column to (first_row + nb_rows - 1) row and (first_col + nb_cols - 1) column.
 *
 * \param *originImage Source image from which the cropped image is done
 * \param *finalImage Destination image which is the cropped result image
 * \param first_row Row of the source image which is the first row of the destination image
 * \param first_col Column of the source image which is the first column of the destination image 
 * \param nb_rows Number of rows of the destination image
 * \param nb_cols Number of columns of the destination image
 */
void crop(const pnm * originImage, pnm * finalImage, int first_row, int first_col, int nb_rows, int nb_cols);


/**
 * \fn resize(const pnm * originImage, pnm * finalImage, int new_nb_cols, int new_nb_rows, int cols, int rows)
 * \brief Resize an image from size (rows, cols) to size (new_nb_rows, new_nb_cols).
 *
 * \param *originImage Source image from which the resized image is done
 * \param *finalImage Destination image which is the resized result image
 * \param new_nb_cols Number of columns of the destination image
 * \param new_nb_rows Number of rows of the destination image
 * \param cols Number of columns of the source image
 * \param rows Number of rows of the source image
 */
void resize(const pnm * originImage, pnm * finalImage, int new_nb_cols, int new_nb_rows, int cols, int rows);


/**
 * \fn reverse(const pnm * originImage, pnm * finalImage, int cols, int rows)
 * \brief Reverse an image.
 *
 * \param *originImage Source image from which the reversed image is done
 * \param *finalImage Destination image which is the reversed result image
 * \param cols Number of columns of both images
 * \param rows Number of rows of both images
 */
void reverse(const pnm * originImage, pnm * finalImage, int cols, int rows);


/**
 * \fn grey(const pnm * originImage, pnm * finalImage, int cols, int rows)
 * \brief Do the black and white image from a image.
 *
 * \param *originImage Source image from which the black and white image is done
 * \param *finalImage Destination image which is the black and white result image
 * \param cols Number of columns of both images
 * \param rows Number of rows of both images
 */
void grey(const pnm * originImage, pnm * finalImage, int cols, int rows);

#endif
