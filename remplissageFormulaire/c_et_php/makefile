## regles a faire avec un parseur avant :
## 1) tous les mots sont entoures de quotes simples 'mot'
## 2) quand il y a une quote/apostrophe (ex : n'aime) on ferme la quote simple avant l'apostrophe, on met entre guillements/quotes doubles l'apostrophe et on ouvre une quote simple apres le guillemets fermant de l'apostrophe 'debut_mot'"'"'fin_mot' (ex : 'n'"'"'aime')

parse:
	php parse.php 'France' 'Jean-Michel' 'DUPOND' 'JeanDUPOND@gmail.com' 'ID.png' '02/02/2016' 'je' 'n'"'"'aime' 'pas' 'cette' 'photo,' 'oui' 'c'"'"'est' 'une' 'virgule!' 'exclamation' 'et' 'les' 'accents' 'é' 'à' 'è' 'ù' 'ê' 'î' 'ô' 'û' 'â' 'ï' 'enfin' 'point.' 'interrogation?' 'caracteres' 'speciaux' 'commentaire\\' 'retourligne\n' '\\' '\n' '//' 'lepluscomplique' '\\n' 'fin_explication' 'http://premiereadereferencer.com' 'http://deuxiemeadereferencer.fr' 'FIN_TEXTE' 'BORNE_SECU'

envoi:
	php formulaire.php 'France' 'Jean-Michel' 'DUPOND' 'JeanDUPOND@gmail.com' 'ID.png' '02/02/2016' 'je' 'n'"'"'aime' 'pas' 'cette' 'photo,' 'oui' 'c'"'"'est' 'une' 'virgule!' 'exclamation' 'et' 'les' 'accents' 'é' 'à' 'è' 'ù' 'ê' 'î' 'ô' 'û' 'â' 'ï' 'enfin' 'point.' 'interrogation?' 'caracteres' 'speciaux' 'commentaire\\' 'retourligne\n' '\\' '\n' '//' 'lepluscomplique' '\\n' 'fin_explication' 'http://premiereadereferencer.com' 'http://deuxiemeadereferencer.fr' 'FIN_TEXTE' 'BORNE_SECU'

tout: Fichier_texte Preparse Exec_liaison ## on doit passer les infos sur l'entree standard de preparse
	./fichier_texte | ./preparse | ./exec_liaison

lien: Fichier_texte Exec_liaison ## on doit passer les infos sur l'entree standard de preparse
	./fichier_texte | ./exec_liaison

preparseur: Fichier_texte Preparse ## on doit passer les infos sur l'entree standard de preparse
	./fichier_texte | ./preparse

opti: Opti
	./opti

version_finale: Fichier_texte Version_finale 
	./fichier_texte | ./version_finale

Fichier_texte:
	gcc -o fichier_texte fichier_texte.c

Preparse:
	gcc -o preparse preparse.c

Exec_liaison:
	gcc -o exec_liaison exec_liaison.c

Opti:
	gcc -o opti opti.c

Version_finale:
	gcc -o version_finale version_finale.c

clean:
	rm *~ fichier_texte exec_liaison preparse opti version_finale
