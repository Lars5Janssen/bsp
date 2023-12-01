#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <dirent.h>
#include <cstring>
#include <unistd.h>

#define Author "Larissa, Lars, Elias"
#define VERSION "1.0"

void runCommand(char command[255]);

int main ()
{
    bool exit = false;

    printf("Aktuelles Verzeichnis: \n");
    int result = system("ls");
    if (result != 0) {
        printf("Couldn't open the directory\n");
    }

    while (!exit) {
        // Wait for user input
        char command[255];
        printf("> ");
        scanf("%s", command);

        if (strcmp(command, "quit") == 0) {
            exit = true;
        } else if (strcmp(command, "version") == 0) {
            printf("HAW-Shell Version %s Autor: %s\n", VERSION, Author);
        } else if (strcmp(command, "help") == 0) {
            printf("available commands are:\n"
                   "quit\n"
                   "version\n"
                   "help\n");
        } else {
            if (command[strlen(command) -1] == '&') {
                if (fork() == 0) {
                    runCommand(command);
                }
            } else {
                runCommand(command);
            }
        }
    }

    return 0;
}

void runCommand(char command[255]) {
    int result = system(command);
    if (result != 0) {
        printf("Couldn't find command: %s\n", command);
    }
}