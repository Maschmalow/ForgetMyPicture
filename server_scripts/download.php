<?php

function treat_file($file_id, $save_location)
{
   try {
        
        // Undefined | Multiple Files | $_FILES Corruption Attack
        // If this request falls under any of them, treat it invalid.
        if (
            !isset($_FILES['selfie']['error'][$file_id]) ||
            is_array($_FILES['selfie']['error'][$file_id])
        ) {
            throw new RuntimeException('Invalid parameters.');
        }

        // Check $_FILES['selfie']['error'][$file_id] value.
        switch ($_FILES['selfie']['error'][$file_id]) {
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
        if ($_FILES['selfie']['size'][$file_id] > 2000000) {
            throw new RuntimeException('Exceeded filesize limit.');
        }


        if (!move_uploaded_file(
            $_FILES['selfie']['tmp_name'][$file_id], $save_location)
        ) {
            throw new RuntimeException('Failed to move uploaded file.');
        }
        echo 'File is uploaded successfully.';

    } catch (RuntimeException $e) {
        echo $e->getMessage();
        http_response_code(400);
        exit();
    }
}

?>