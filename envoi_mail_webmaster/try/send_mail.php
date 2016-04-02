<?php
require 'PHPMailer-master/PHPMailerAutoload.php';

$mail = new PHPMailer;

//$mail->SMTPDebug = 3;                               // Enable verbose debug output

$mail->isSMTP();                                      // Set mailer to use SMTP
$mail->Host = 'smtp.enseirb-matmeca.fr';  // Specify main and backup SMTP servers
$mail->SMTPAuth = true;                               // Enable SMTP authentication
$mail->Username = 'adurand??????@enseirb-matmeca.fr';                 // SMTP username
$mail->Password = '????';                           // SMTP password
$mail->SMTPSecure = 'tls';                            // Enable TLS encryption, `ssl` also accepted
$mail->Port = 25;                                    // TCP port to connect to

$mail->setFrom('adurand??????@enseirb-matmeca.fr', 'ForgetMyPicture');    
$mail->addAddress('ellen@example.com');               // Add a recipient, name is optional
$mail->addCC('cc@example.com');

$mail->isHTML(true);                                  // Set email format to HTML

$mail->Subject = 'Detrimental photo problem';
$mail->Body    = 'Dear Sir or Madam,<br><br>On your Internet domain, I have found a detrimental photo of me, which breaches my rights and my image. I would like to draw your attention to this situation.<br>Indeed, under Article 16 (ex Article 286 TEC) Title II of the Treaty on European Union and the Treaty on the Functioning of the European Union, and the Data Protection Act, I request to delete the photo [photo URL] of your Internet domain.<br>By two months, you have to respond to my request. Please contact me at the following email address [user’s email address].<br>I should like also to remind you that, under the Data Protection Act, if an individual suffers damage, they are entitled to claim compensation from you.<br>In the absence of action from you, I see myself compelled to take legal action by exercising later my right to object.<br>Thanking you in advance for your diligence,<br>Yours faithfully,<br>[First name NAME]';

if(!$mail->send()) {
  echo 'Message could not be sent.';
  echo 'Mailer Error: ' . $mail->ErrorInfo;
 } else {
  echo 'Message has been sent';
 }
?>
