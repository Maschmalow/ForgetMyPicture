test_parseur: Who_is Parseur
	@rm -rf error.txt
	@echo
	@echo For the URL: http://enseirb-matmeca.fr/:
	@./who_is http://enseirb-matmeca.fr/ > adr_mail.txt
	@./parseur adr_mail.txt client_email_adr@gmail.com first_name name URL_photo fr
	@echo
	@sleep 1 # Need to wait a little because whois requests are rejected if it is too fast between two whois requests
	@echo For the URL: http://fr.atos.net/fr-fr/accueil.html:
	@./who_is http://fr.atos.net/fr-fr/accueil.html > adr_mail.txt
	@./parseur adr_mail.txt client_email_adr@gmail.com first_name name URL_photo en
	@echo
	@sleep 1
	@echo For the URL: https://www.google.fr/:
	@./who_is https://www.google.fr/ > adr_mail.txt
	@./parseur adr_mail.txt client_email_adr@gmail.com first_name name URL_photo fr
	@echo
	@sleep 1
	@echo For the URL: http://www.bbc.co.uk/:
	@./who_is http://www.bbc.co.uk/ > adr_mail.txt
	@./parseur adr_mail.txt client_email_adr@gmail.com first_name name URL_photo en
	@echo
	@sleep 1
	@echo For the URL: https://www.legifrance.gouv.fr/Droit-francais:
	@./who_is https://www.legifrance.gouv.fr/Droit-francais > adr_mail.txt
	@./parseur adr_mail.txt client_email_adr@gmail.com first_name name URL_photo fr
	@echo
	@sleep 1
	@echo For the URL: https://www.atositchallenge.net/ideas/selected-teams:
	@./who_is https://www.atositchallenge.net/ideas/selected-teams > adr_mail.txt
	@./parseur adr_mail.txt client_email_adr@gmail.com first_name name URL_photo fr
	@echo
	@sleep 1
	@echo For the URL: https://fr.wikipedia.org/wiki/Wikip%C3%A9dia:Accueil_principal:
	@./who_is https://fr.wikipedia.org/wiki/Wikip%C3%A9dia:Accueil_principal > adr_mail.txt
	@./parseur adr_mail.txt client_email_adr@gmail.com first_name name URL_photo ed

test_who_is: Who_is
# Need to wait a little between two tests
	@echo
	@echo For the URL: http://enseirb-matmeca.fr/:
	@./who_is http://enseirb-matmeca.fr/

Parseur:
	gcc -DTEST_OR_SEND=0 -o parseur have_adr.c

Have_adr:
	gcc -DTEST_OR_SEND=1 -o have_adr have_adr.c

Who_is:
	gcc -o  who_is who_is.c

cleanall:
	rm -rf who_is parseur have_adr adr_mail.txt *~

.PHONY :
	clean all
