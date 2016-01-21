%% MAIN PROGRAM OF FACE DETECTION

clear all
close all
clc

% Detect faces using Viola-Jones Algorithm 
% people.jpg is an image to be inserted

I = imread('people.jpg');
[faces] = detectFaces(I);

