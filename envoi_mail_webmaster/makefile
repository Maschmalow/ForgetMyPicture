test_parseur: Who_is Parseur
	./who_is enseirb-matmeca.fr > adr_mail.txt
	./parseur adr_mail.txt

test_mail: Who_is Envoi_mail
	./who_is enseirb-matmeca.fr > adr_mail.txt
	./envoi_mail adr_mail.txt

Parseur:
	gcc -o parseur parseur.c

Envoi_mail:
	gcc -o envoi_mail envoi_mail.c

Who_is:
	gcc -o who_is who_is.c

clean:
	rm *~ who_is parseur envoi_mail adr_mail.txt
