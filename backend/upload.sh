#!/bin/bash

source .secret

lftp -c "set ftp:list-options -a;
set ftp:ssl-allow no;
open ftp://$FTP_USER:$FTP_PASSWORD@$FTP_HOST;
put api.php"
