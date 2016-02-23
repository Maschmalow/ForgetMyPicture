<?php

///////////debut ~parseur//////////////

$country = $argv[1];
$name = $argv[2] . ' ' . $argv[3];
$mail_adr = $argv[4];
$piece_identite = $argv[5];
$date = $argv[6];


$i=7;
$explication = ''; //$explication doit toujours au moins avoir un mot, ce qui devrait etre toujours le cas
                   //$explication contient le texte former des mots jusqu'au mot fin_explication qui sert de balise de fin
                   //$explication ne peux pas contenir d'apostrophe, ni tout autre symbole ayant un sens dans une chaine de caracteres (par exemples : \n, (, ), ...)
		   //, car c'est interprete et donc ca provoque un bug.
		   //Une solution a ce bug serait de faire passer le texte dans un parseur (pas en php, mais plutot en C/C++) qui enlève toutes ces symboles en les traduisant directement en code URL avec une fonction de type urlencode()
		   //, a ce moment la, il faudra enlever urlencode() des 3 boucles foreach 
while($argv[$i] != 'fin_explication')
  {
    $explication .= $argv[$i] . ' ';
    $i++;
  }
$explication = substr($explication, 0, -1);
$i++;

//'url_box3' comme clef
$list_adr = array(); //URLs a dereferencer, le plus dur a remplir 
while($argv[$i] != 'FIN_TEXTE') //FIN_TEXTE est une balise pour indiquer la fin du texte
  {
    $list_adr[] = $argv[$i]; //ex : http://premiereadereferencer.com , http://deuxiemeadereferencer.fr
    $i++;
  }

///////////fin ~parseur////////////////


//pour les variables telles que $country, $name, ... soit on essaie de les recuperer dans la ligne de commande, soit on met ces donnees dans un fichier et on essaie de les recuperer dedans
$data = array();
$data['selected_country'] = $country; //ex : France
$data['name_searched'] = $name; //ex : Jean DUPOND
$data['requestor_name'] = $name; //ex : Jean DUPOND
$data['contact_email_noprefill'] = $mail_adr; //ex : JeanDUPOND@gmail.com

//ici pour les URLs, donc on prend les 4 premiers de data d'ou : $i < 4

$data['eudpa_explain'] = $explication; // ex : http://exemple_1.com
                                       //      Cette URL me concerne, car… 
                                       //      Cette page ne devrait pas faire partie des resultats de recherche, car…
$data['legal_idupload'] = $piece_identite; // ex : carte_identitee.png
$data['eudpa_consent_statement'] = 'agree'; // on met les '' car c'est une donnee (du texte)
$data['signature'] = $name; //ex : Jean DUPOND
$data['signature_date'] = $date; //format MM/DD/YYYY, ex : 01/30/2016


$post_str = '';
$i = 0;
foreach($data as $key => $value)
{
  if($i < 4)
    {
      $post_str .= $key . '=' . urlencode($value) . '&';
    }
  $i++;
}

foreach($list_adr as $value)
{
  $post_str .= 'urlbox3' . '=' . urlencode($value) . '&';
}

$i = 0;
foreach($data as $key => $value)
{
  if($i >= 4)
    {
      $post_str .= $key . '=' . urlencode($value) . '&';
    }
  $i++;
}

$post_str = substr($post_str, 0, -1);

echo $post_str . "\n";
?>
