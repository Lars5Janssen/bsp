#!/bin/bash 
# ?
# Der Anti Chris 
# 3.11.23

# ------------------------------------------------------------
# This function shows the help text for this bash script
usage() { 
   echo "
   $0 [OPTIONS] [<user name>]
   LKdjhas;lfhjasdfnals;dkjfaslk;djf;lasdf 
   OPTIONS: 
      -h: Display this help
   "
}

# ---------------------- main --------------------------------
# check parameters 
if [ $# -gt 1 ]; then
    usage
    exit 1
fi

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
