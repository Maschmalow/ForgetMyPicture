test_parseur: Who_is Parseur
# Need to wait a little between two tests
	@echo
	@echo For the address: enseirb-matmeca.fr:
	@./who_is enseirb-matmeca.fr > adr_mail.txt
	@./parseur adr_mail.txt
	@echo
	@sleep 1 # Need to wait a little because whois requests are rejected if it is too fast between two whois requests
	@echo For the address: atos.net:
	@./who_is atos.net > adr_mail.txt
	@./parseur adr_mail.txt
	@echo
	@sleep 1
	@echo For the address: google.fr:
	@./who_is google.fr > adr_mail.txt
	@./parseur adr_mail.txt
	@echo
	@sleep 1
	@echo For the address: bbc.co.uk:
	@./who_is bbc.co.uk > adr_mail.txt
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
	@echo
	@sleep 1
# To test with an error in the address email file 
	@echo For the address: enseirb-matmeca.fr:
	@echo @!@!@! > adr_mail.txt
	@./who_is enseirb-matmeca.fr >> adr_mail.txt
	@./parseur adr_mail.txt

test_mail: Who_is Send_mail # a changer
	@echo
	@echo For the address: enseirb-matmeca.fr:
	@./who_is enseirb-matmeca.fr > adr_mail.txt
	@./send_mail adr_mail.txt

Parseur:
	gcc -o parseur parseur.c

Send_mail:
	gcc -o send_mail send_mail.c

Who_is:
	gcc -o  who_is who_is.c

cleanall:
	rm -rf who_is parseur send_mail adr_mail.txt *~

.PHONY :
	clean all
