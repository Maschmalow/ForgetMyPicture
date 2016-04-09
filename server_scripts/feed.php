<?php

if(!isset($_POST, $_POST['result'], $_POST['deviceId'],  $_POST['requestId']) ) {
    http_response_code(400);
    exit();
}

require 'rb.php';
R::setup('sqlite:/var/databases/ForgetMyPicture.db');


$user = R::load('user', $_POST['deviceId']);
if($user->deviceId == '0')  {
    http_response_code(400);
    exit();
}

$request = R::load('request', sprintf('%d_%d', $_POST['deviceId'], $_POST['requestId']));
if($request->id == '0')  {
    http_response_code(400);
    exit();
}

foreach ($resultURL as $_POST['result']) {
    $result = R::dispense('request');
    $result->picURL = $resultURL;
    $result->match = -1;
    $result>request_id = $request->id;
    R::store($result);
}

R::close();
?>