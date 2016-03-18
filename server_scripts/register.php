<?php
require 'rb.php';
R::setup('sqlite:/var/databases/forgetmypicture.db');

$hash = _POST['hash']
// check hash here

$user = R::dispense('user')
$user->deviceId = _POST['deviceId']
$user->name = _POST['name']
$user->forename = _POST['forename']
$user->email = _POST['email']

$base_path = sprintf('/var/databases/files/%s_', _POST['deviceId'])
$user->idCard = $base_path + 'idCard.png'
treat_file('idcard', $base_path + 'idCard.png')
$user->selfie = $base_path + 'selfie.png'
treat_file('selfie', $base_path + 'selfie.png')

R::close();



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
    }
}

?>