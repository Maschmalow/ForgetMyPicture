<?php


if(!isset($_POST, $_FILES, $_FILES['selfie'], $_POST['h'], $_POST['deviceId']) || $_POST['h'] != '1') {
    http_response_code(400);
    exit();
}

require 'rb.php';
include 'download.php';
R::setup('mysql:host=localhost;dbname=forgetmypicture', 'apache', 'AtosApache2016');


$selfies_count = 0;
foreach ($_FILES['selfie']['name'] as $file_id => $val) {
        $path = sprintf('/var/databases/files/%s_selfie_%s.png', $_POST['deviceId'], $selfies_count);
        treat_file($file_id, $path);
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