<?php

if(!isset($_GET, $_GET['deviceId']) ) {
    http_response_code(400);
    exit();
}

require 'rb.php';
R::setup('mysql:host=localhost;dbname=forgetmypicture', 'apache', 'AtosApache2016');

$user = R::load('user', $_GET['deviceId']);
if($user->deviceId == '0')  {
    http_response_code(400);
    exit();
}

$ret = array();
foreach(R::find('request', 'user_id = ?', [$user->deviceId]) as $request) {
    $ret[$request->id] = array();
    foreach(R::find('result', 'request_id = ?', [$request->id]) as $result) {
        if($result->match != -1) {
            $ret[$request->id][$result->picURL] = $result->match;
            R::trash($result);
        }
    }
}

echo json_encode($ret, JSON_FORCE_OBJECT);


R::close();
?>