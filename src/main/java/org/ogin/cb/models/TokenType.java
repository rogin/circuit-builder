package org.ogin.cb.models;

public enum TokenType {
   // Pseudoterminals
   IDENTIFIER,INTEGER,EOFX,UNKNOWN,
   // Reserved words
   COMPONENTS,
   ALIASES,
   CONNECTIONS,
   END,
   AND,NAND,OR,NOR,XOR,NOT,LATCHX,BUFFERX,
   IN,
   OUT,
   POWER,
   GROUND,
   // Delimiters
   STAR,  /*  * */
   POUND, /*  # */
   DASH,  /*  - */
   EQUAL;  /*  = */
}