#include <stdio.h>
#include <stdlib.h>
#include <cstring>
#include <unistd.h>
#include <sys/wait.h>
#include <string>

#define Author "Larissa, Lars, Elias"
#define VERSION "1.0"

#define BUFSIZE 256

void runCommand(char command[BUFSIZE]);

char *getCurrDir(char[]);

int main ()
{
    bool exit = false;
    char Buffer[BUFSIZE];

    while (!exit) {
        // Wait for user input
        char command[255];
        printf("%s > ", getCurrDir(Buffer));
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
            int status = 0;
            int PID = fork();

            if (command[strlen(command) -1] == '&') {
                command[strlen(command) - 1] = '\0';
            }

            if (PID == 0) {
                runCommand(command);
            }
            if (command[strlen(command) -1] != '&') {
                waitpid(PID, &status, 0);
            }

            if (status != 0) {
                printf("Befehl %s nicht gefunden", command);
            }
        }
    }

    return 0;
}

void runCommand(char command[BUFSIZE]) {
    char commandStart[] = "/bin/";

    size_t len1 = strlen(commandStart);
    size_t len2 = strlen(command);

    char* result = new char[len1 + len2 + 1];
    strcpy(result, commandStart);
    strcat(result, command);

    printf("\n%s\n", result);

    execl(result, "hawsh-child", (char *)NULL);
    // If fail
    perror("execl");
}

char *getCurrDir(char Buffer[BUFSIZE]) {
    char* result;

    result = getcwd(Buffer, BUFSIZE);

    if( result == nullptr ){
        printf("getcwd failed!\n");
        exit(1);
    }

    return Buffer;
}