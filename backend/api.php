<?php

$USERS_FILENAME = 'users.json';
$GROUPS_FILENAME = 'groups.json';

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

function db_remove_by_id($filename) {
    $id = $_POST['id'];

    if (!isset($id) || !is_numeric($id))
        fatal_error("Bad or missing 'id' parameter!");

    $entries = db_load($filename);
    $idx = db_get_index_by_id($entries, $id);

    if ($idx < 0)
        fatal_error("Entry with id $id not found!");

    array_splice($entries, $idx, 1);
    db_save($filename, $entries);
    echo("OK");
}

function db_insert_with_auto_id_into_file($filename, $new_entry) {
    $entries = db_load($filename);
    $id = db_insert_with_auto_id($entries, $new_entry);
    db_save($filename, $entries);

    echo("$id");
}

// --------------------------------------------------------------------------------------------------------------------

function users_list() {
    global $USERS_FILENAME;

    header('Content-Type: application/json; charset=utf-8');
    echo(file_get_contents($USERS_FILENAME));
}

function users_list_filtered() {
    global $USERS_FILENAME;

    $groupId = $_POST['groupId'];

    if (!isset($groupId) || !is_numeric($groupId))
        fatal_error("Bad or missing 'groupId' parameter!");

    $users = db_load($USERS_FILENAME);
    $filteredUsers = array();

    foreach ($users as &$u) {
        if ($u['groupId'] == $groupId)
            array_push($filteredUsers, $u);
    }

    header('Content-Type: application/json; charset=utf-8');
    echo(json_encode($filteredUsers));
}

function users_add() {
    global $USERS_FILENAME;

    $name = $_POST['name'];
    $phone = $_POST['phone'];

    if (!isset($name) || !isset($phone))
        fatal_error("Bad parameter(s)!");

    $new_user['name'] = $name;
    $new_user['phone'] = $phone;

    db_insert_with_auto_id_into_file($USERS_FILENAME, $new_user);
}

function users_remove() {
    global $USERS_FILENAME;

    db_remove_by_id($USERS_FILENAME);
}

function group_list() {
    global $GROUPS_FILENAME;

    header('Content-Type: application/json; charset=utf-8');
    echo(file_get_contents($GROUPS_FILENAME));
}

function group_add() {
    global $GROUPS_FILENAME;

    $name = $_POST['name'];
    $owner = $_POST['owner'];

    if (!isset($name) || !isset($owner) || !is_numeric($owner))
        fatal_error("Bad parameter(s)!");

    $new_group['name'] = $name;
    $new_group['owner'] = (int)$owner;

    db_insert_with_auto_id_into_file($GROUPS_FILENAME, $new_group);
}

function group_remove() {
    global $GROUPS_FILENAME;

    db_remove_by_id($GROUPS_FILENAME);
}

function join_group() {
    global $USERS_FILENAME;
    global $GROUPS_FILENAME;

    $userId = $_POST['userId'];
    $groupId = $_POST['groupId'];

    if (!isset($userId) || !is_numeric($userId) || !isset($groupId) || !is_numeric($groupId))
        fatal_error("Bad or missing parameter(s)!");

    $users = db_load($USERS_FILENAME);
    $userIdx = db_get_index_by_id($users, $userId);
    if ($userIdx < 0)
        fatal_error("User ($userId) not found");

    if ($groupId != 0) {
        $groups = db_load($GROUPS_FILENAME);
        if (db_get_index_by_id($groups, $groupId) < 0)
            fatal_error("Group ($groupId) not found");
    }

    $users[$userIdx]['groupId'] = (int)$groupId;
    db_save($USERS_FILENAME, $users);
    echo("OK");
}

// --------------------------------------------------------------------------------------------------------------------

switch($_GET['command']) {
    case 'user-list-all':
        users_list();
        break;
    case 'user-add':
        users_add();
        break;
    case 'user-remove':
        users_remove();
        break;
    case 'user-list':
        users_list_filtered();
        break;
    case 'join-group':
        join_group();
        break;
    case 'group-list':
        group_list();
        break;
    case 'group-add':
        group_add();
        break;
    case 'group-remove':
        group_remove();
        break;
    default:
        fatal_error("Unknown command");
}

?>
