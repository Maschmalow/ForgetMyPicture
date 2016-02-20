test_mail: Who_is Parseur
	./who_is enseirb-matmeca.fr > mail.txt
	./parseur mail.txt

Parseur:
	gcc -o parseur parseur.c

Who_is:
	gcc -o who_is who_is.c

clean:
	rm *~ who_is parseur mail.txt
