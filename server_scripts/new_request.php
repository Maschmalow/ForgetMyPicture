<?php

if(!isset( $_POST, $_POST['deviceId'],  $_POST['requestId']) ) {
    http_response_code(400);
    exit();
}



require 'rb.php';
include 'download.php';
R::setup('mysql:host=localhost;dbname=forgetmypicture', 'apache', 'AtosApache2016');

ob_start();  
var_export($_POST); 
$tab_debug1=ob_get_contents(); 
ob_end_clean(); 
$fichier2=fopen('post.txt','w+'); 
fwrite($fichier2,$tab_debug1); 
fclose($fichier2); 

//$user = R::load('user', $_POST['deviceId']);
$user = R::findOne('user', 'device_id = ?', array($_POST['deviceId']));

ob_start(); 
var_export($user); 
$tab_debug2=ob_get_contents(); 
ob_end_clean(); 
$fichier3=fopen('request.txt','w+'); 
fwrite($fichier3,$tab_debug2); 
fclose($fichier3); 

if($user->deviceId == '0')  {
    http_response_code(400);
    exit();
}



$request = R::dispense('request');
$request->user_id = $user->deviceId;
$request->id = sprintf("%s_%s", $user->deviceId, $_POST['requestId']);
if(empty($_FILES['originalPic']['name'])) {
    $request->kind = 'EXAUSHTIVE';
    $request->originalPicPath = NULL;
    //$request->original_pic_path = -1;
} else if(count($_FILES['originalPic']['name']) == 1) {
    $path = sprintf('/var/databases/files/%s_originalPic.png', $request->id);
    treat_file($_FILES['originalPic']['name'], $path);
    $request->originalPicPath = $path;
    $request->kind = 'QUICK';
} else {
    http_response_code(400);
    exit();
}



ob_start();  
var_export($request); 
$tab_debug=ob_get_contents(); 
ob_end_clean(); 
$fichier=fopen('test.txt','w+'); 
fwrite($fichier,$tab_debug); 
fclose($fichier); 

R::store($request);

R::close();
?>