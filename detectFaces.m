function [faces] = detectFaces(I)



% To detect Face
FDetect = vision.CascadeObjectDetector;

% Returns Bounding Box values based on number of objects
BB = step(FDetect,I);

figure(1)

imshow(I);

hold on


% select the zone
for i = 1:size(BB,1)

    rectangle('Position',BB(i,:),'LineWidth',5,'LineStyle','-','EdgeColor','r');

end


title('Face Detection');

hold off;


% Extraction of the face and allocation of faces structure

faces=struct([]);

for i=1:size(BB,1)

A=I(BB(i,2):BB(i,2)+BB(i,4),BB(i,1):BB(i,1)+BB(i,3),:);

faces(1,i).field=int2str(i);

faces(1,i).value=A;

figure(2)

imshow(faces(1,i).value);

pause(3)

end


end

