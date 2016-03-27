<?php
require 'rb.php';
include 'download.php'
R::setup('sqlite:/var/databases/ForgetMyPicture.db');


$user = R::load('user', $_POST['deviceId']);
if($user->deviceId == '0')  {
    http_response_code(400);
    exit();
}

$request = R::dispense('request');
$request->user_id = $user->deviceId;
$request->id = sprintf("%s_%s", $user->deviceId, $_POST['requestId']);
if(count($_FILES) == 0) {
    $request->kind = 'EXAUSHTIVE';
    $request->originalPicPath = NULL
} else if(count($_FILES) == 1) {
    $path = sprintf('/var/databases/files/%s_originalPic.png', $request->id);
    treat_file(array_pop($_FILES), $path);
    $request->originalPicPath = $path
    $request->kind = 'QUICK';
} else {
    http_response_code(400);
    exit();
}

R::store($request)

R::close();
?>