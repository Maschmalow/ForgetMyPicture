## regles a faire avec un parseur avant :
## 1) tous les mots sont entoures de quotes simples 'mot'
## 2) quand il y a une quote/apostrophe (ex : n'aime) on ferme la quote simple avant l'apostrophe, on met entre guillements/quotes doubles l'apostrophe et on ouvre une quote simple apres le guillemets fermant de l'apostrophe 'debut_mot'"'"'fin_mot' (ex : 'n'"'"'aime')

parse:
	php parse.php 'France' 'Jean-Michel' 'DUPOND' 'JeanDUPOND@gmail.com' 'ID.png' '02/02/2016' 'je' 'n'"'"'aime' 'pas' 'cette' 'photo,' 'oui' 'c'"'"'est' 'une' 'virgule!' 'exclamation' 'et' 'les' 'accents' 'é' 'à' 'è' 'ù' 'ê' 'î' 'ô' 'û' 'â' 'ï' 'enfin' 'point.' 'interrogation?' 'fin_explication' 'http://premiereadereferencer.com' 'http://deuxiemeadereferencer.fr'

envoi:
	php formulaire.php 'France' 'Jean-Michel' 'DUPOND' 'JeanDUPOND@gmail.com' 'ID.png' '02/02/2016' 'je' 'n'"'"'aime' 'pas' 'cette' 'photo,' 'oui' 'c'"'"'est' 'une' 'virgule!' 'exclamation' 'et' 'les' 'accents' 'é' 'à' 'è' 'ù' 'ê' 'î' 'ô' 'û' 'â' 'ï' 'enfin' 'point.' 'interrogation?' 'fin_explication' 'http://premiereadereferencer.com' 'http://deuxiemeadereferencer.fr'

clean:
	rm *~
