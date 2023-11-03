/*
* <Description of this C program>
*
* Elias Wernicke # 03.11.2023
*
* Compile: gcc -o saveuser saveuser.c
*/

#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <string.h>

int main () {
	// get filename
	printf("Name der neuen Datei: ");
	char Filename[31];
	if (fgets(Filename, 30, stdin) != NULL) {
		size_t len = strlen(Filename);
		if (len > 0 && Filename[len - 1] == '\n') {
			Filename[--len] = '\0';
		} else {
			printf("Error, invalid filename!");
		}
	}

	// get UUID
	uid_t UID = getuid();

	// create file
	FILE *file = fopen(Filename, "w");
	if (file == NULL) {
		printf("Error, file couldn't be created!");
	} else {
		fprintf(file, "%u", UID);
		printf("Die Datei %s wurde erfolgreich erzeugt", Filename);
	}
	fclose(file);

	return 0;
}
