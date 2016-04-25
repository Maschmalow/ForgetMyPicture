/**
 * \file send_mail.php
 * \brief Launch the process to send mail to website host administrators
 * \author Pierre PLUMIER
 *
 * \param $argv[1] URL of the targeted website
 * \param $argv[2] User's e-mail address
 * \param $argv[3] User's First name
 * \param $argv[4] User's LAST NAME
 * \param $argv[5] Photo URL
 * \param $argv[6] Citizenship : for the prototype the value can only be uk for British people or fr for French people
 */
<?php
system('./who_is '. $argv[1] .' > adr_mail.txt');
system('./have_adr adr_mail.txt '. $argv[2] .' '. $argv[3] .' '. $argv[4] .' '. $argv[5] .' '. $argv[6] );
system('sleep 1') // Need to wait a little because whois requests are rejected if it is too fast between two whois requests
?>
