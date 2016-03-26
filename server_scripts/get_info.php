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
    foreach($result as R::find('result', 'request_id = ?', [$request->id]) {
        if($result->match != -1)
            
    }
}



R::close();
?>