#include "opencv2/core/core.hpp"
#include "opencv2/contrib/contrib.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/objdetect/objdetect.hpp"

#include <iostream>
#include <fstream>
#include <sstream>
#include <stdio.h>

#define USR 0
#define MAX_DIS 7000.0
#define EIGEN 1
#define FISHER 2
#define LBPH 4


using namespace std;
using namespace cv;

/** Function Headers */
Mat detectFace(Mat frame);
void readCsv(const string& filename, vector<Mat>& images, vector<int>& labels, char separator = ';');

/** Global variables */
String faceCascadeName = "haarcascade_frontalface_alt.xml";
String eyeCascadeName = "haarcascade_eye_tree_eyeglasses.xml";
String pathCsv = "testReco.csv";
CascadeClassifier face_cascade;
CascadeClassifier eye_cascade;

/** @function main */
int main(int argc, const char** argv)
{
	vector<Mat> images;
	vector<int> labels;
	
	// Load cascade
	if (!face_cascade.load(faceCascadeName)) 
	{
		return -1; 
	}
	if (!eye_cascade.load(eyeCascadeName)) 
	{
		return -1; 
	}
	
	/* Adding user pictures to the recognizer */
	int count = argc - 2;
	for(int i = 0; i < count; i++)
	{
		Mat m = imread(string(argv[i+2]));
		if(!m.empty())
		{
			Mat m2 = detectFace(m);
			if(m2.channels() != 1)
			{
				cvtColor(m2, m2, CV_BGR2GRAY);
			}
			resize(m2, m2, Size(150, 150), 0, 0, INTER_NEAREST);
			images.push_back(m2);
			labels.push_back(USR);
		}
	}
	
	// Reading the data
	readCsv(pathCsv, images, labels);

	// Getting dimensions for resizing purpose
	int im_width = images[0].cols;
    int im_height = images[0].rows;

	
	
	// Resizing the pictures (needed for the recognizer to work)
	for(int i = 0; i < images.size(); i++)
	{
		resize(images[i],images[i],images[0].size(), 0, 0, INTER_NEAREST);
	}
	
	// Creating the Recognizers
    int recognition = EIGEN | FISHER | LBPH;
    Ptr<FaceRecognizer> modelF, modelE, modelL;
	if(recognition & FISHER)
	{
		modelF = createFisherFaceRecognizer();
		modelF->set("threshold", MAX_DIS);
		modelF->train(images, labels);
	}
	if(recognition & EIGEN){
		modelE = createEigenFaceRecognizer();
		modelE->set("threshold", MAX_DIS);
		modelE->train(images, labels);
	}
	if(recognition & LBPH)
	{
		modelL = createLBPHFaceRecognizer();
		modelL->set("threshold", 50.0);
		modelL->train(images, labels);
	}
	
	/* Testing the image given */
	string path = string(argv[1]);
	Mat Image = imread(path);
	if(Image.empty())
	{
		return -1;
	}
	Mat gray;
    cvtColor(Image, gray, CV_BGR2GRAY);
    vector< Rect_<int> > faces;
    // Face detection
    face_cascade.detectMultiScale(gray, faces, 1.1, 3, 0 | CV_HAAR_SCALE_IMAGE, Size(30,30));
    double tmp = 400000.0;
	for(int i = 0; i < faces.size(); i++) 
	{
		Rect face_i = faces[i];
		Mat face = gray(face_i);
		Mat face_resized;
        resize(face, face_resized, Size(im_width, im_height), 1.0, 1.0, INTER_CUBIC);
        		
        if(recognition & FISHER)
        {
			int prediction = -1;
			double dis = 0.0;
			modelF->predict(face_resized, prediction, dis);
			if(prediction == USR && tmp > dis)
			{
					tmp = dis;
			}	
		}
		if(recognition & EIGEN)
        {
			int prediction = -1;
			double dis = 0.0;
			modelE->predict(face_resized, prediction, dis);
			if(prediction == USR && tmp > dis)
			{
					tmp = dis;
			}	
		}
		/*
		 * hard to combine with the other two, does not give the same distance (different scale)
		if(recognition & LBPH)
        {
			int prediction = -1;
			double dis = 0.0;
			modelL->predict(face_resized, prediction, dis);
			if(prediction == USR && tmp > dis)
			{
					tmp = dis;
			}	
		}
		* */
	}
	
	/* Printing the likelihood of the user to be in the image */
	tmp = (MAX_DIS - tmp)/ MAX_DIS * 100;
	int res = (int) tmp;
	res = (res < 0) ? 0 : res;
	printf("%d\n", res);
	return res;
}

/* Detect the user face in the selfie
 * Can have bad result in some cases (if face detection detects another face)
 */
Mat detectFace(Mat frame)
{
	std::vector<Rect> faces;
	Mat frameGray;
	cvtColor(frame, frameGray, CV_BGR2GRAY);
	equalizeHist(frameGray, frameGray);

	// Detect face
	face_cascade.detectMultiScale(frameGray, faces, 1.1, 2, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));
	if(faces.size() <= 0)
	{
		return frame;
	}
	Rect face_i = faces[0];
	Mat face = frame(face_i);
	return face;
}

/* Pushes the preselected database images for the recognition in images and labels vectors */
void readCsv(const string& filename, vector<Mat>& images, vector<int>& labels, char separator)
{
	std::ifstream file(filename.c_str(), ifstream::in);
	if (!file)
	{
		string error_message = "No valid input file was given, please check the given filename.";
		CV_Error(CV_StsBadArg, error_message);
	}
	string line, path, classlabel;
	while (getline(file, line))
	{
		stringstream liness(line);
		getline(liness, path, separator);
		getline(liness, classlabel);
		if (!path.empty() && !classlabel.empty()) 
		{
			Mat m = imread(path, 0);
			if ( m.empty() )
			{
				 cout<<path;
				 return;
			}
			//cvtColor(m, m, CV_BGR2GRAY);
			images.push_back(m);
			labels.push_back(atoi(classlabel.c_str()));
		}
	}
}
