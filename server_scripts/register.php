<?php
require 'rb.php';
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

function treat_file($filename, $save_location)
{
   try {
        
        // Undefined | Multiple Files | $_FILES Corruption Attack
        // If this request falls under any of them, treat it invalid.
        if (
            !isset($_FILES[$filename]['error']) ||
            is_array($_FILES[$filename]['error'])
        ) {
            throw new RuntimeException('Invalid parameters.');
        }

        // Check $_FILES[$filename]['error'] value.
        switch ($_FILES[$filename]['error']) {
            case UPLOAD_ERR_OK:
                break;
            case UPLOAD_ERR_NO_FILE:
                throw new RuntimeException('No file sent.');
            case UPLOAD_ERR_INI_SIZE:
            case UPLOAD_ERR_FORM_SIZE:
                throw new RuntimeException('Exceeded filesize limit.');
            default:
                throw new RuntimeException('Unknown errors.');
        }

        // You should also check filesize here. 
        if ($_FILES[$filename]['size'] > 2000000) {
            throw new RuntimeException('Exceeded filesize limit.');
        }


        if (!move_uploaded_file(
            $_FILES[$filename]['tmp_name'], $save_location)
        )) {
            throw new RuntimeException('Failed to move uploaded file.');
        }
        echo 'File is uploaded successfully.';

    } catch (RuntimeException $e) {
        echo $e->getMessage();
        http_response_code(400)
        exit()
    }
}

function startswith($haystack, $needle) {
    return substr($haystack, 0, strlen($needle)) === $needle;
}

?>