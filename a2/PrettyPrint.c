
/*
 * PrettyPrint.c Version 1.6
 *
 * @author Prof. Dr.-Ing. Martin Hübner
 * Zweck: Eine C-Sourcecode-Datei mit Einrückungen nach { } versehen
 * Aufruf: PrettyPrint <Source-Dateiname>.c
 * Ausgabe: <Source-Dateiname>-pp.c im selben Verzeichnis
 * Falls kein Argument angegeben: Interaktive Abfrage des Source-Dateinamens
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>
#include <stdbool.h>

#define MAX_LL 255  // Maximale Länge einer Zeile
#define MAX_FN 99   // Maximale Länge eines Dateinamens

/* ------------ Globale Variablen ----------*/
const bool TEST_MODE = false;
const char OPENING_BRACKET[] = "{";
const char CLOSING_BRACKET[] = "}";

char outFileName[MAX_FN+1];   // Dateiname der Ausgabedatei
int indentation = 3;          // Anzahl Leerzeichen für EINE Einrückung

char inLineBuf[MAX_LL+1];     // Aktuelle Eingabezeile
char* inLine;  				   // Zeiger auf den Textanfang in inLineBuf
char outLineBuf[MAX_LL+1];    // Aktuelle Ausgabezeile

bool commentMode = false;
bool lineContinuationMode = false;
int lineCommentPos = 1000000;   // Position von // in einer Zeile

/* ------------ Funktionen ----------*/
void setoutLine(int spaceCount);
int checkContinuationMode (char* line);
int countMatches (const char* string, const char* suchString, int maxPos);
int strpos (const char* string, const char* suchString);
char* trim(char* s);
char* rtrim(char* s);
bool startsWith(char* s, const char* start);
bool endsWith(char* s, const char* end);
int min(int val1, int val2);
void testout(char* out);


/*
 * @param Dateiname
 *            einer C-Source-Datei
 * @param OPTIONAL
 *            Anzahl Leerzeichen an Einrückung (indentation)
 * @return Eine Datei <Source-Dateiname>-pp.<ext> im selben Verzeichnis (wird ggf.
 *         überschrieben)
 */

int main(int argc, char* argv[]) {
   /* Variablen */
   char inFileName[MAX_FN+1];
   FILE *inFile;
   FILE *outFile;
   char* extPos;                 // Position des letzten "." im Source-Dateinamen
   char extension[10];           // Extension im Source-Dateinamen (".c" oder ".java")

   int bracketDiff = 0; // Differenz Anzahl { - Anzahl }
   int curIndent = 0;
   int commentDiff = 0; // Differenz Anzahl /* - Anzahl */
   int lineCounter = 0;

   /* Begrüßung */
   printf("\n");
   printf("%s\n","************************************************************************");
   printf("%s\n","Willkommen zum einfachen C-PrettyPrinter v1.6!");
   printf("%s\n","Zweck: Sourcecode formatieren (Einrueckungen) anhand von { und }.");
   printf("%s\n","(C) Prof. Dr.-Ing. Martin Huebner, HAW Hamburg ");

   if (argc == 1) {
      // Kein Argument beim Aufruf --> Eingabedateinamen abfragen
      printf("\nBitte Dateinamen der zu formatierenden Sourcecode-Eingabedatei (z.B. prog.c) eingeben: \n");
      scanf("%100s", inFileName);
   } else
   {
      // 1. Argument verwenden
      strcpy(inFileName, argv[1]);
   }

   /* Eingabedatei öffnen */
   inFile = fopen(inFileName, "r");
   if(NULL == inFile) {
      printf("-------> Fehler: Konnte Datei %s nicht oeffnen!\n", inFileName);
      return EXIT_FAILURE;
   }

   /* Optionales 2. Argument: indentation-Konstante */
   if (argc > 2) {
      sscanf(argv[2], "%d", &indentation);
      if (indentation < 1 || indentation > 10){
         indentation = 3;
      }
   }

   /* Ausgabedatei-Name festlegen: <Sourcedateiname>-pp.<ext> */
   strncpy(outFileName, inFileName, strlen(inFileName));
   // Pointer auf letzten "." im Ausgabedateinamen-String setzen
   extPos = strrchr(outFileName, '.');
   // Extension speichern (inkl. ".")
   strcpy(extension, extPos);
   // "-pp" anhängen
   strcpy(extPos, "-pp");
   // Extension anhängen
   strcat(outFileName, extension);

   /* Ausgabedatei öffnen */
   outFile = fopen(outFileName, "w");
   if(NULL == outFile) {
      printf("-------> Fehler: Konnte Datei %s nicht oeffnen!\n", outFileName);
      return EXIT_FAILURE;
   }

   /* ---------- Jede Zeile lesen und verarbeiten ---------------- */
   printf("\nEingabedatei %s wird formatiert ..", inFileName);
   while (fgets(inLineBuf, MAX_LL+1, inFile) != NULL) {
      /* Leerzeichen und newline in inLineBuf eliminieren */
      inLine = trim(inLineBuf);
      lineCounter++;
      lineCommentPos = strpos(inLine,"//"); //-Kommentar gefunden? --> Stelle merken

      commentDiff = countMatches(inLine, "/*", lineCommentPos)
            - countMatches(inLine, "*/", lineCommentPos);
      if (commentMode) {
         /* --------- noch im CommentMode ---------- */
         setoutLine(curIndent);
         if (commentDiff < 0) {
            /* --------- CommentMode ausschalten ---------- */
            commentMode = false;
            curIndent = curIndent - 1; //indentation
         }
      } else {
         /* --------- kein bestehender CommentMode ---------- */
         if (commentDiff > 0 && startsWith(inLine, "/*")) {
            /* --------- in CommentMode wechseln ---------- */
            commentMode = true;
            /* Aktuelle Zeile erzeugen, danach nach rechts einrücken */
            setoutLine(curIndent);
            curIndent = curIndent + 1; //indentation
         } else {
            /* --------- normale Anweisung ---------- */
            /* Nach { und } suchen */
            bracketDiff = countMatches(inLine, OPENING_BRACKET, lineCommentPos)
                  - countMatches(inLine, CLOSING_BRACKET, lineCommentPos);
            if (bracketDiff > 0) {
               /* Einrückung setzen */
               setoutLine(curIndent);
               curIndent = curIndent + indentation * bracketDiff;
            } else {
               if (bracketDiff < 0) {
                  /* Ausrückung setzen */
                  curIndent = curIndent + indentation * bracketDiff; // diff ist NEGATIV!
                  if (curIndent < 0){
                     curIndent = 0;
                  }
                  setoutLine(curIndent);
               } else {
                  /* Klammerzahl gleich */
                  if (strstr(inLine, OPENING_BRACKET) > strstr(inLine, CLOSING_BRACKET)) {
                  /* schließende vor öffnender Klammer, z.B. } else { */
                     curIndent = curIndent - indentation;
                     setoutLine(curIndent);
                     curIndent = curIndent + indentation;
                  } else {
                     /* keine Veränderung */
                     setoutLine(curIndent);
                  }
               }
            }
         }
      }
      testout(outLineBuf);  // Testausgabe
      /* Ausgabezeile mit angehängtem newline schreiben */
      fputs(outLineBuf, outFile);
      fputs("\n", outFile);
   }

   /* Dateien schließen */
   fclose(inFile);
   fclose(outFile);

   /* Verabschiedung */
   printf(".. fertig! Es wurden %d Zeilen erzeugt.\n", lineCounter);
   printf("\nAusgabedatei: %s\n", outFileName);
   printf("************************************************************************\n");

   return EXIT_SUCCESS;
}

void setoutLine(int spaceCount) {
   /* Erzeugen einer Ausgabezeile outLineBuf mit Einrückung von spaceCount */
   char* outLine;   // Zeiger auf Textanfang in OutLineBuf
   int inLineLen;   // Laenge des Zeilentextes

   /* Argument pruefen */
   if (spaceCount < 0) {
      spaceCount = 0;
   }

   /* Bei unvollständiger Anweisung 2. Teil zusätzlich einrücken (Fortsetzung)*/
   if (lineContinuationMode && !startsWith(inLine, OPENING_BRACKET)) {
      spaceCount = spaceCount + 2 * indentation;
   }

   /* Laenge des Zeilentextes pruefen: MAX_LL - spaceCount = Platz in outline */
   inLineLen = strlen(inLine);
   if (inLineLen > MAX_LL - spaceCount){
      spaceCount = MAX_LL - inLineLen;
   }
   // outlineBuf initialisieren mit Leerzeichen
   memset(outLineBuf,' ', MAX_LL);
   // Anfangsadresse des Ausgabetextes in outlineBuf ermitteln
   outLine = &outLineBuf[spaceCount];
   // inLine nach outLineBuf kopieren (ab Zeiger outLine)
   strncpy(outLine, inLine, inLineLen);
   // Leerzeichen am Ende von outLineBuf eliminieren
   rtrim(outLineBuf);

   /* Prüfen, ob die Zeile unvollständig ist, damit in der
    NÄCHSTEN Zeile fortgesetzt wird (Zusatzeinrückung einer Zeile) */
   if (lineCommentPos < MAX_LL){
      //-Kommentar in der Zeile:
      // Kopie von inLine verwenden, in der der Text ab // eliminiert ist
      char inLineWithoutComment[MAX_LL+1];
      strncpy(inLineWithoutComment, inLine, lineCommentPos);
      inLineWithoutComment[lineCommentPos] = '\0';
      lineContinuationMode = checkContinuationMode(trim(inLineWithoutComment));
   } else {
      // Keine Zeilenkommentar vorhanden --> komplette Zeile analysieren
      lineContinuationMode = checkContinuationMode(inLine);
   }
}

/* Rückgabe true, wenn eine Anweisung fortgesetzt wird */
int checkContinuationMode (char* line) {
   int result = true;

   if (commentMode ||
         strlen(line) == 0 ||
         startsWith(line, "#") ||
         endsWith(line, OPENING_BRACKET) ||
         endsWith(line, CLOSING_BRACKET) ||
         endsWith(line, ";") ||
         endsWith(line, ":") ||
         endsWith(line, "*/")){
      result = false;
   }
   return result;
}

/* Rückgabe: Die Anzahl des Vorkommens von suchString in string
 * Der Suchstring wird nur maximal bis zum Index maxPos durchsucht */
int countMatches (const char* string, const char* suchString, int maxPos){
   int count = 0;
   if (string != NULL && suchString != NULL && strlen(string) >= strlen(suchString)){

      /* Argumente sind gültig */
      int suchLaenge = strlen(suchString);
      int maxStart = min(strlen(string) - suchLaenge, maxPos);

      for (int i = 0; i <= maxStart;i++){
         /* Jede mögliche Start-Position auf Übereinstimmung mit dem suchString prüfen */
         if (strncmp(&string[i], suchString, suchLaenge) == 0){
            count++;
         }
      }
   }
   return count;
}

/* Liefert den ersten Index, an dem der suchString im string vorkommt,
 * ansonsten 1000000 */
int strpos (const char* string, const char* suchString){
   int index = 1000000;
   bool gefunden = false;
   if (string != NULL && suchString != NULL && strlen(string) >= strlen(suchString)){

      /* Argumente sind gültig */
      int suchLaenge = strlen(suchString);
      int maxStart = strlen(string) - suchLaenge;

      for (int i = 0; i <= maxStart && !gefunden;i++){
         /* Jede mögliche Start-Position auf Übereinstimmung mit dem suchString prüfen */
         if (strncmp(&string[i], suchString, suchLaenge) == 0){
            gefunden = true;
            index = i;
         }
      }
   }
   return index;
}

/* Entfernt Leerzeichen und newlines vom Anfang eines Strings
 * Hilfsfunktion für trim
 * Rückgabe: ein Zeiger auf den Beginn des Textes */
char* ltrim(char* string){
   int i = 0;
   for (i = 0; (isspace (string[i])); i++){};
   return &string[i];
}

/* Entfernt Leerzeichen und newlines vom Ende eines Strings
 * Hilfsfunktion für trim
 * Rueckgabe: ein Zeiger auf den Beginn des Textes */
char* rtrim(char* string){
   if (strlen(string) > 0) {
      int i = strlen (string) - 1;
      while(i >= 0 && isspace(string[i])) {
         i--;
      }
      string[i + 1] = '\0';
   }
   return string;
}

/* Entfernt Leerzeichen und newlines vom Anfang und Ende eines Strings
 * Rückgabe: ein Zeiger auf den Beginn des Textes */
char* trim(char* s){
   return rtrim(ltrim(s));
}

/* Prüft, ob string mit der Zeichenfolge search beginnt.
 * Rückgabe: true / false */
bool startsWith(char* string, const char* search){
   bool result = false;
   if (strncmp(string, search, strlen(search)) == 0){
      result = true;
   }
   return result;
}

/* Prüft, ob string mit der Zeichenfolge search endet.
 * Rückgabe: true / false */
bool endsWith(char* string, const char* search){
   bool result = false;
   int lenString = strlen(string);
   int lenSearch = strlen(search);

   if (string != NULL && lenString > 0) {
      if (strncmp(&string[lenString-lenSearch], search, lenSearch) == 0){
         result = true;
      }
   }
   return result;
}

/* Ersatz von Math.fmin durch int-Version
 * wegen Ubuntu-Compilerproblem: -lm muss sonst beim Compilieren ans Ende gestellt werden! */
int min(int val1, int val2){
   int minVal = val1;  // Default-Ergebnis
   if (val1 > val2) {
      minVal = val2;
   }
   return minVal;
}


/* Gibt die Zeichenfolge auf der Konsole aus, wenn TEST_MODE true ist. */
void testout(char* s) {
   if (TEST_MODE){
      printf("%s\n", s);
   }
}
