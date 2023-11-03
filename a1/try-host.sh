#!/bin/bash 
# Monitors host name for availiability
# 3.11.23

# ------------------------------------------------------------
# This function shows the help text for this bash script
usage() { 
   echo "
   $0 [<host name>]
   Monitors host name for availiability 
   "
}

# ---------------------- check prameters --------------------------------
# check parameters 
if [ $# -gt 0 || ! -n "$1"]; then
    usage
    exit 1
fi

# ---------------------- main --------------------------------
while true
do
    if ping -c 1 -W 1 $1 > /dev/null 2>&1; then
        echo "$1 OK"
    else
        echo "$1 FAILED"
    fi
sleep 1
done

exit 0 

# ---------------------- end ---------------------------------
