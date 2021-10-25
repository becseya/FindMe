<?php

$USERS_FILENAME = 'users.json';

function fatal_error($description) {
    http_response_code(400);
    echo("$description\n");
    exit;
}

// --------------------------------------------------------------------------------------------------------------------

function db_load($path) {
    return json_decode(file_get_contents($path), true);
}

function db_save($path, $entries) {
    $fp = fopen($path, 'w');
    fwrite($fp, json_encode($entries));
    fclose($fp);
}

function db_get_index_by_id(&$entries, $id) {
    foreach ($entries as $index => $entry) {
        if ($entry['id'] == $id)
            return $index;
    }

    return -1;
}

function db_insert_with_auto_id(&$entries, $new_entry) {
    if (sizeof($entries) == 0)
        $entries = array();

    do {
        $id = rand();
    } while((db_get_index_by_id($entries, $id) != -1) || ($id == 0));

    $new_entry['id'] = $id;
    array_push($entries, $new_entry);
    return $id;
}

// --------------------------------------------------------------------------------------------------------------------

function users_list() {
    global $USERS_FILENAME;

    header('Content-Type: application/json; charset=utf-8');
    echo(file_get_contents($USERS_FILENAME));
}

function users_add() {
    global $USERS_FILENAME;

    $name = $_POST['name'];
    $phone = $_POST['phone'];

    if (!isset($name) || !isset($phone))
        fatal_error("Bad parameter(s)!");

    $new_user['name'] = $name;
    $new_user['phone'] = $phone;

    $users = db_load($USERS_FILENAME);
    $id = db_insert_with_auto_id($users, $new_user);
    db_save($USERS_FILENAME, $users);

    echo("$id");
}

function users_remove() {
    global $USERS_FILENAME;

    $id = $_POST['id'];

    if (!isset($id) || !is_numeric($id))
        fatal_error("Bad or missing 'id' parameter!");

    $users = db_load($USERS_FILENAME);
    $idx = db_get_index_by_id($users, $id);

    if ($idx < 0)
        fatal_error("User with id $id not found!");

    array_splice($users, $idx, 1);
    db_save($USERS_FILENAME, $users);
    echo("OK");
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
