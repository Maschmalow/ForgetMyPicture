test_parseur: Who_is Parseur
	@echo
	@echo For the URL: http://enseirb-matmeca.fr/:
	@./who_is http://enseirb-matmeca.fr/ > adr_mail.txt
	@./parseur adr_mail.txt
	@echo
	@sleep 1 # Need to wait a little because whois requests are rejected if it is too fast between two whois requests
	@echo For the URL: http://fr.atos.net/fr-fr/accueil.html:
	@./who_is http://fr.atos.net/fr-fr/accueil.html > adr_mail.txt
	@./parseur adr_mail.txt
	@echo
	@sleep 1
	@echo For the URL: https://www.google.fr/:
	@./who_is https://www.google.fr/ > adr_mail.txt
	@./parseur adr_mail.txt
	@echo
	@sleep 1
	@echo For the URL: http://www.bbc.co.uk/:
	@./who_is http://www.bbc.co.uk/ > adr_mail.txt
	@./parseur adr_mail.txt
	@echo
	@sleep 1
	@echo For the URL: https://www.legifrance.gouv.fr/Droit-francais:
	@./who_is https://www.legifrance.gouv.fr/Droit-francais > adr_mail.txt
	@./parseur adr_mail.txt
	@echo
	@sleep 1
	@echo For the URL: https://www.atositchallenge.net/ideas/selected-teams:
	@./who_is https://www.atositchallenge.net/ideas/selected-teams > adr_mail.txt
	@./parseur adr_mail.txt
	@echo
	@sleep 1
	@echo For the URL: https://fr.wikipedia.org/wiki/Wikip%C3%A9dia:Accueil_principal:
	@./who_is https://fr.wikipedia.org/wiki/Wikip%C3%A9dia:Accueil_principal > adr_mail.txt
	@./parseur adr_mail.txt

test_who_is: Who_is
# Need to wait a little between two tests
	@echo
	@echo For the URL: http://enseirb-matmeca.fr/:
	@./who_is http://enseirb-matmeca.fr/

test_mail: Who_is Send_mail # a changer
	@echo
	@echo For the address: enseirb-matmeca.fr:
	@./who_is enseirb-matmeca.fr > adr_mail.txt
	@./send_mail adr_mail.txt

Parseur:
	gcc -DTEST_OR_SEND=0 -o parseur send_mail.c

Send_mail:
	gcc -DTEST_OR_SEND=1 -o send_mail send_mail.c

Who_is:
	gcc -o  who_is who_is.c

cleanall:
	rm -rf who_is parseur send_mail adr_mail.txt *~

.PHONY :
	clean all
