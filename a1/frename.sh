#!/bin/bash 
# <Rename all files in a directory appending a postfix>
# <Jansen, Wernecke, Pychlau> 
# <2023-11-03>

# ------------------------------------------------------------
# This function shows the help text for this bash script
usage() { 
   echo "
   $0 [OPTIONS] [<user name>]
   Rename all files in a directory, appending a postfix. The first parameter specifies the directory, the second the postfix. 
   OPTIONS: 
      -h: Display this help
   "
}

# This function renames files in a given directory
rename_files() {
    directory="$1"
    postfix="$2"

    if [ ! -d "$directory" ]; then
        echo "The directory $directory does not exist."
        exit 1
    fi
    for file in "$directory"/*; do

        if [ -f "$file" ]; then

            filename=$(basename "$file")

            if [[ $filename == *.* ]]; then

                extension="${filename##*.}"
                
                new_filename="${filename%.*}$postfix.$extension"

            else 
                new_filename="${filename%.*}$postfix"
            fi

            mv "$file" "$directory/$new_filename"
            echo "Renamed: $filename -> $new_filename"
        fi
    done
}

# ---------------------- main --------------------------------
# check parameters 

if [ $# -ne 2 ]; then
    usage
    exit 1
fi

case $1 in
    "-h")
        usage
        exit 0
        ;;
    *)
        rename_files "$1" "$2"
	;;
esac

# print greetings
echo "#################### Your files have been renamed. ####################"
exit 0 

# ---------------------- end ---------------------------------