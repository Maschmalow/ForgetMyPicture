test_parseur: Who_is Parseur
	./who_is enseirb-matmeca.fr > adr_mail.txt
	./parseur adr_mail.txt

test_mail: Who_is Send_mail
	./who_is enseirb-matmeca.fr > adr_mail.txt
	./send_mail adr_mail.txt

Parseur:
	gcc -o parseur parseur.c

Send_mail:
	gcc -o send_mail send_mail.c

Who_is:
	gcc -o who_is who_is.c

cleanall:
	rm -rf who_is parseur send_mail adr_mail.txt *~

.PHONY :
	clean all
