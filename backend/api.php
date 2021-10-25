<?php

$USERS_FILENAME = 'users.json';

function fatal_error($description) {
    http_response_code(400);
    echo("$description\n");
    exit;
}

// --------------------------------------------------------------------------------------------------------------------

function users_list() {
    global $USERS_FILENAME;

    header('Content-Type: application/json; charset=utf-8');
    echo(file_get_contents($USERS_FILENAME));
}

function users_add() {
    echo("User added!\n");
}

function users_remove() {
    echo("User removed!\n");
}

// --------------------------------------------------------------------------------------------------------------------

switch($_GET['command']) {
    case 'user-add':
        users_add();
        break;
    case 'user-remove':
        users_remove();
        break;
    case 'user-list':
        users_list();
        break;
    default:
        fatal_error("Unknown command");
}

?>
