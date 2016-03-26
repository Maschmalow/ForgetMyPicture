<?php
require 'rb.php';
R::setup('sqlite:/var/databases/ForgetMyPicture.db');

$user = R::load('user', $_POST['deviceId']);
if($user->deviceId == '0')  {
    http_response_code(400);
    exit();
}

$ret = array()
foreach( $request as R::find('request', 'user_id = ?', [$user->deviceId])) {
    $ret[$request->id] = array()
    foreach($result as R::find('result', 'request_id = ?', [$request->id]) {
        if($result->match != -1) {
            $ret[$request->id][$result->picURL] = $result->match;
            R::trash($result)
        }
    }
}

echo json_encode($ret, JSON_FORCE_OBJECT);


R::close();
?>