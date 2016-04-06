<?php
require 'PHPMailer-master/PHPMailerAutoload.php';

$mail = new PHPMailer;

$mail->SMTPDebug = 2;                               // Enable verbose debug output

$mail->isSMTP();                                      // Set mailer to use SMTP
$mail->Host = 'smtp.gmail.com';  // Specify main and backup SMTP servers
$mail->SMTPAuth = true;                               // Enable SMTP authentication
$mail->Username = 'ForgetMyPicture@gmail.com';                 // SMTP username
$mail->Password = '******';                           // SMTP password
$mail->SMTPSecure = 'tls';                            // Enable TLS encryption, `ssl` also accepted
$mail->Port = 465;  //465 gmail, 25 enseirb            // TCP port to connect to
//$mail->Helo = 'enseirb.fr';                        // Set the SMTP HELO of the message (Default is $Hostname)

$mail->setFrom('ForgetMyPicture@gmail.com', 'ForgetMyPicture');
$mail->addAddress($argv[1]);               // Add a recipient, name is optional
$mail->addReplyTo($argv[2]);           // Add a "Reply-to" address
$mail->addCC($argv[2]);          // Add a "Cc" address. Note: this function works with the SMTP mailer on win32, not with the "mail" mailer

$mail->isHTML(true);                                  // Set email format to HTML

$mail->Subject = 'Detrimental photo problem';
$mail->Body    = 'Dear Sir or Madam,<br><br>On your Internet domain, I have found a detrimental photo of me, which breaches my rights and my image. I would like to draw your attention to this situation.<br>Indeed, under Article 16 (ex Article 286 TEC) Title II of the Treaty on European Union and the Treaty on the Functioning of the European Union, and the Data Protection Act, I request to delete the photo ' . $argv[5] . ' of your Internet domain.<br>By two months, you have to respond to my request. Please contact me at the following email address' . $argv[2] . ' .<br>I should like also to remind you that, under the Data Protection Act, if an individual suffers damage, they are entitled to claim compensation from you.<br>In the absence of action from you, I see myself compelled to take legal action by exercising later my right to object.<br>Thanking you in advance for your diligence,<br>Yours faithfully,<br>' . $argv[3] . $argv[4];

if(!$mail->send()) {
  echo 'Message could not be sent.';
  echo 'Mailer Error: ' . $mail->ErrorInfo;
 } else {
  echo 'Message has been sent';
 }
?>
