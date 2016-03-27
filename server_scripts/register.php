<?php
require 'rb.php';
include 'download.php'
R::setup('sqlite:/var/databases/ForgetMyPicture.db');


$hash = $_POST['hash'];
if($hash != '1') {
    http_response_code(400);
    exit();
}


$selfies_count = 0
foreach ($filename as $_FILES) {
        $path = sprintf('/var/databases/files/%s_selfie_%s.png', $_POST['deviceId'], $selfies_count);
        treat_file($filename, $path);
        $selfie = R::dispense('selfie');
        $selfie->user_id = $_POST['deviceId'];
        $selfie->path = $path;
        R::store($selfie);
        $selfies_count++;
}

$user = R::load('user', $_POST['deviceId']);
$user->deviceId = $_POST['deviceId'];
$user->email = $_POST['email'];
R::store($user);


R::close();

http_response_code(200);


?>