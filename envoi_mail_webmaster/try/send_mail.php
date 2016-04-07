<?php
require 'PHPMailer-master/PHPMailerAutoload.php';

$mail = new PHPMailer;

$mail->SMTPDebug = 2;                               // Enable verbose debug output

$mail->isSMTP();                                      // Set mailer to use SMTP
$mail->Host = 'smtp.gmail.com';  // Specify main and backup SMTP servers
$mail->SMTPAuth = true;                               // Enable SMTP authentication
$mail->Username = 'ForgetMyPicture@gmail.com';                 // SMTP username
$mail->Password = '****';                           // SMTP password
$mail->SMTPSecure = 'ssl';                            // Enable TLS encryption, `ssl` also accepted
$mail->Port = 465;  //465 gmail, 25 enseirb            // TCP port to connect to
//$mail->Helo = 'enseirb.fr';                        // Set the SMTP HELO of the message (Default is $Hostname)

$mail->setFrom('ForgetMyPicture@gmail.com', 'ForgetMyPicture');
$mail->addAddress($argv[1]);               // Add a recipient, name is optional
$mail->addReplyTo($argv[2]);           // Add a "Reply-to" address
$mail->addCC($argv[2]);          // Add a "Cc" address. Note: this function works with the SMTP mailer on win32, not with the "mail" mailer

$mail->isHTML(true);                                  // Set email format to HTML

$mail->Subject = 'Detrimental photo problem';

$which_text = 1; // $argv[7] == 'uk' or no $argv[7]

if ($argc == 7)
  {
    if ($argv[6] == 'fr')
      $which_text = 2;
  }


// Returning to the line is important to begin a new paragraph
switch($which_text)
  {
  case 1:
    $text_1_eng_sentence_1 = 'Dear Sir or Madam,


';
    $text_1_eng_sentence_2 = 'On your Internet domain, I have found a detrimental photo of me, which breaches my rights and my image. I would like to draw your attention to this situation.

';
    $text_1_eng_sentence_3 = 'Indeed, under Article 16 (ex Article 286 TEC) Title II of the Treaty on European Union and the Treaty on the Functioning of the European Union, and the Data Protection Act, I request to delete the photo ' . $argv[5] . ' of your Internet domain.

';
    $text_1_eng_sentence_4 = 'By two months, you have to respond to my request. Please contact me at the following email address ' . $argv[2] . ' .

';
    $text_1_eng_sentence_5 = 'I should like also to remind you that, under the Data Protection Act, if an individual suffers damage, they are entitled to claim compensation from you.

';
    $text_1_eng_sentence_6 = 'In the absence of action from you, I see myself compelled to take legal action by exercising later my right to object.

';
    $text_1_eng_sentence_7 = 'Thanking you in advance for your diligence,

';
    $text_1_eng_sentence_8 = 'Yours faithfully,

';
    $text_1_eng_sentence_9 = $argv[3] . ' ' . $argv[4];

    $mail->Body    = $text_1_eng_sentence_1 . '<br><br><br>' . $text_1_eng_sentence_2 . '<br><br>' . $text_1_eng_sentence_3 . '<br><br>' . $text_1_eng_sentence_4 . '<br><br>' . $text_1_eng_sentence_5 . '<br><br>' . $text_1_eng_sentence_6 . '<br><br>' . $text_1_eng_sentence_7 . '<br><br>' . $text_1_eng_sentence_8 . '<br><br>' . $text_1_eng_sentence_9;    // This is the HTML message body

    $mail->AltBody = $text_1_eng_sentence_1 . $text_1_eng_sentence_2 . $text_1_eng_sentence_3 . $text_1_eng_sentence_4 . $text_1_eng_sentence_5 . $text_1_eng_sentence_6 . $text_1_eng_sentence_7 . $text_1_eng_sentence_8 . $text_1_eng_sentence_9;               // This is the body in plain text for non-HTML mail clients
    break;
  case 2:
     $separation = '---------------------------------------

';

    // English version
    $text_2_eng_sentence_1 = 'Dear Sir or Madam,


';
    $text_2_eng_sentence_2 = 'On your Internet domain, I have found a detrimental photo of me, which breaches my rights and my image. I would like to draw your attention to this situation.

';
    $text_2_eng_sentence_3 = 'Indeed, under Article 38 of the law "Informatique et Libertés"  of the French law, I request to delete the photo ' . $argv[5] . ' of your Internet domain.

';
    $text_2_eng_sentence_4 = 'According to Article 94 of the Decree of 20 October 2005 of the French law, you have a maximum of two months of receipt of this letter in response to my request. Please contact me at the following email address ' . $argv[2] . ' .

';
    $text_2_eng_sentence_5a = 'I should like also to remind you that:
';
    $text_2_eng_sentence_5b = 'Under Article 226-18-1 of the Criminal Code of the French law is punishable by five years imprisonment and €300,000 fine the fact of conducting a personal data processing relating to an individual despite the person\'s opposition when this processing is for marketing purposes  notably commercial ones, or where the opposition is based on legitimate reasons.
';
    $text_2_eng_sentence_5c = 'Under R625-12 of the Criminal Code of the French law is liable to a fine for contraventions of the fifth class, for a responsible of personal data automated processing, the act of not to proceeding, at no cost to the applicant, with the operations requested by an individual, upon proof of his identity, who requires to be rectified, completed, updated , locked or deleted personal data concerning him or concerning the deceased of whom the individual is an heir, when these data are inaccurate, incomplete, misleading, outdated , or where the collection, use, communication or storage is prohibited.
';
    $text_2_eng_sentence_5d = 'Under Article 226-1 of the Criminal Code of the French law is punishable by one year imprisonment and €45,000 fine the fact of violating the privacy of other people\'s privacy fixing, recording or transmitting, without the consent of the latter, the image of a person in a private place.
';
    $text_2_eng_sentence_5e = 'Under Article 226-8 of the Criminal Code of the French law is punishable by one year imprisonment and €15,000 fine the fact publish, by any means whatsoever, the montage with the image of a person without their consent, it does not appear clearly that it is a montage or if it is not expressly mentioned.

';
    $text_2_eng_sentence_6 = 'In the absence of action from you, I see myself compelled to take legal action by exercising later my right to object.

';
    $text_2_eng_sentence_7 = 'Thanking you in advance for your diligence,

';
    $text_2_eng_sentence_8 = 'Yours faithfully,

';
    $text_2_eng_sentence_9 = $argv[3] . ' ' . $argv[4] . '

';

    // French version

    $text_2_fr_sentence_1 = 'Madame, Monsieur,


';
    $text_2_fr_sentence_2 = 'Ayant constaté sur votre domaine Internet la présence d\'une photo me concernant, dont j\'estime que son sujet est en infraction avec mes droits, je me permets de vous contacter afin de vous en avertir.

';
    $text_2_fr_sentence_3 = 'En effet, en vertu de l\'article 38 de la loi “Informatique et Libertés”, je vous demande de bien vouloir supprimer et dépublier la photo ' . $argv[5] . ' de votre domaine Internet.


';
    $text_2_fr_sentence_4 = 'Conformément à l\'article 94 du décret du 20 octobre 2005, vous disposez d\'un délai maximal de deux mois suivant la réception de ce courrier pour répondre à ma demande. Veuillez me contacter à l\'adresse mail suivante ' . $argv[2] . ' .

';
    $text_2_fr_sentence_5a = 'Je vous rappelle que :
';
    $text_2_fr_sentence_5b = 'D\'après l\'article 226-18-1 du code pénal, est puni de cinq ans d\'emprisonnement et de 300 000 euros d\'amende, le fait de procéder à un traitement de données à caractère personnel concernant une personne physique malgré l\'opposition de cette personne, lorsque ce traitement répond à des fins de prospection, notamment commerciale, ou lorsque cette opposition est fondée sur des motifs légitimes.
';
    $text_2_fr_sentence_5c = 'D\'après l\'article R625-12du code pénal, est puni de l\'amende prévue pour les contraventions de la cinquième classe le fait, pour le responsable d\'un traitement automatisé de données à caractère personnel, de ne pas procéder, sans frais pour le demandeur, aux opérations demandées par une personne physique justifiant de son identité et qui exige que soient rectifiées, complétées, mises à jour, verrouillées ou effacées les données à caractère personnel la concernant ou concernant la personne décédée dont elle est l\'héritière, lorsque ces données sont inexactes, incomplètes, équivoques, périmées, ou lorsque leur collecte, leur utilisation, leur communication ou leur conservation est interdite.
';
    $text_2_fr_sentence_5d = 'D\'après l\'article 226-1 du code pénal, est punit d\'un an d\'emprisonnement et 45 000 euros d\'amende le fait de porter atteinte à l\'intimité de la vie privée d\'autrui en fixant, enregistrant ou transmettant, sans le consentement de celle-ci, l\'image d\'une personne se trouvant dans un lieu privé.
';
    $text_2_fr_sentence_5e = 'D\'après l\'article 226-8 du code pénal, est punit d\'un an d\'emprisonnement et de 15 000 euros d\'amende le fait de publier, par quelque voie que ce soit, le montage réalisé avec l\'image d\'une personne sans son consentement, s\'il n\'apparaît pas à l\'évidence qu\'il s\'agit d\'un montage ou s\'il n\'en est pas expressément fait mention.

';
    $text_2_fr_sentence_6 = 'En l\'absence d\'action de votre part, je me verrai dans l\'obligation d\'engager des actions juridiques en exerçant par la suite mon droit d\'opposition.

';
    $text_2_fr_sentence_7 = 'Vous remerciant par avance de votre diligence,

';
    $text_2_fr_sentence_8 = 'Bien cordialement,

';
    $text_2_fr_sentence_9 = $argv[3] . ' ' . $argv[4];

    $mail->Body    = $text_2_eng_sentence_1 . '<br><br><br>' . $text_2_eng_sentence_2 . '<br><br>' . $text_2_eng_sentence_3 . '<br><br>' . $text_2_eng_sentence_4 . '<br><br>' . $text_2_eng_sentence_5a . '<br>' . $text_2_eng_sentence_5b . '<br>' . $text_2_eng_sentence_5c . '<br>' . $text_2_eng_sentence_5d . '<br>' . $text_2_eng_sentence_5e . '<br><br>' . $text_2_eng_sentence_6 . '<br><br>' . $text_2_eng_sentence_7 . '<br><br>' . $text_2_eng_sentence_8 . '<br><br>' . $text_2_eng_sentence_9 . '<br><br>' . $separation . '<br><br>' . $text_2_fr_sentence_1 . '<br><br><br>' . $text_2_fr_sentence_2 . '<br><br>' . $text_2_fr_sentence_3 . '<br><br>' . $text_2_fr_sentence_4 . '<br><br>' . $text_2_fr_sentence_5a . '<br>' . $text_2_fr_sentence_5b . '<br>' . $text_2_fr_sentence_5c . '<br>' . $text_2_fr_sentence_5d . '<br>' . $text_2_fr_sentence_5e . '<br><br>' . $text_2_fr_sentence_6 . '<br><br>' . $text_2_fr_sentence_7 . '<br><br>' . $text_2_fr_sentence_8 . '<br><br>' . $text_2_fr_sentence_9;    // This is the HTML message body

    $mail->AltBody = $text_2_eng_sentence_1 . $text_2_eng_sentence_2 . $text_2_eng_sentence_3 . $text_2_eng_sentence_4 . $text_2_eng_sentence_5a . $text_2_eng_sentence_5b . $text_2_eng_sentence_5c . $text_2_eng_sentence_5d . $text_2_eng_sentence_5e . $text_2_eng_sentence_6 . $text_2_eng_sentence_7 . $text_2_eng_sentence_8 . $text_2_eng_sentence_9 . $separation . $text_2_fr_sentence_1 . $text_2_fr_sentence_2 . $text_2_fr_sentence_3 . $text_2_fr_sentence_4 . $text_2_fr_sentence_5a . $text_2_fr_sentence_5b . $text_2_fr_sentence_5c . $text_2_fr_sentence_5d . $text_2_fr_sentence_5e . $text_2_fr_sentence_6 . $text_2_fr_sentence_7 . $text_2_fr_sentence_8 . $text_2_fr_sentence_9;               // This is the body in plain text for non-HTML mail clients
    break;
  }


if(!$mail->send()) {
  echo 'Message could not be sent.';
  echo 'Mailer Error: ' . $mail->ErrorInfo;
 } else {
  echo 'Message has been sent';
 }
?>
