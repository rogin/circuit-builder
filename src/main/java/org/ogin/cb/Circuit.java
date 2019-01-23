package org.ogin.cb;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.ogin.cb.models.*;
import org.ogin.cb.parser.*;

public class Circuit {
    //private char[] fileName; //Constants.IDENTIFIERLENGTH+1
    
    //private int   characterNumber,pageNumber;

    //used within SimulateLogic()
    private StateType  INs[] = new StateType[Constants.MAXIMUMINOUTS+1];
    private StateType OUTs[] = new StateType[Constants.MAXIMUMINOUTS+1];

    private int numberOfComponents;
    private COMPONENT[] components = new COMPONENT[Constants.MAXIMUMCOMPONENTS+1];
    private int numberOfAliases;
    private ALIAS[] aliases = new ALIAS[Constants.MAXIMUMALIASES+1];
    private int numberOfConnections;
    private CONNECTION[] connections = new CONNECTION[Constants.MAXIMUMCONNECTIONS+1];

    //private FILE  *SOURCE,*LOG;
    private char  sourceLine[] = new char[Constants.LINELENGTH+1];
    private char nextCharacter;
    private int   sourceLineIndex,lineNumber;
    private boolean atEOF,atEOL;

    private Iterator<String> sourceLinesIterator;
    private char[] lexeme = new char[Constants.LINELENGTH+1];
    
    public Circuit() {
        numberOfComponents = 0;
        numberOfAliases = 0;
        numberOfConnections = 0;
    }

    public CircuitData getData() {
      CircuitData data = new CircuitData();

      List<COMPONENT> componentList = new ArrayList<>(numberOfComponents);
      for(int i = 1; i <= numberOfComponents; i++) {
         componentList.add(components[i]);
      }
      data.setComponents(componentList);

      List<ALIAS> aliasList = new ArrayList<>(numberOfAliases);
      for(int i = 1; i <= numberOfAliases; i++) {
        aliasList.add(toExportableAlias(aliases[i]));
      }
      data.setAliases(aliasList);

      List<CONNECTION> connectionList = new ArrayList<>(numberOfConnections);
      for(int i = 1; i <= numberOfConnections; i++) {
         connectionList.add(toExportableConnection(connections[i]));
      }
      data.setConnections(connectionList);

      return data;
   }

   private CONNECTION toExportableConnection(CONNECTION input) {
      return new CONNECTION(toExportableNode(input.getNode1()), toExportableNode((input.getNode2())));
   }

   private ALIAS toExportableAlias(ALIAS input) {
     return new ALIAS(input.getIdentifier(), toExportableNode(input.getNode()));
   }

   private NODE toExportableNode(NODE inputNode) {
      NODE newNode = new NODE();
      newNode.integer = inputNode.integer;
      newNode.INOrOUT = inputNode.INOrOUT;
      newNode.index = inputNode.index;

      //pointed to a user-defined component which is 1-based
      if(newNode.index <= numberOfComponents) {
         newNode.index --;
      }

      return newNode;
   }

   /**
    * Load a file according to Dr. Hanna's spec where the filename lacks the ".sdl" extension.
    * @param sourceFileName
    * @throws SDLException
    */
   public void load(String sourceFileName) throws SDLException {
      String fullFilename = sourceFileName + ".sdl";
      ReadFile(fullFilename);
      //verify can write log file
      // strcpy(fullFilename,sourceFilename);
      // strcat(fullFilename,".log");
      // if ( (LOG = fopen(fullFilename,"w")) == NULL) {
      //     ProcessRuntimeError(ErrorType.ERROR_OPENING_LOG_FILE,fullFilename);
      // }
      ParseCircuit();
      // fprintf(LOG,"%4d characters, %3d lines\n",characterNumber,lineNumber);
      // fprintf(LOG," %3d components (%s...%s)\n",numberOfComponents,
      // components[1]->identifier,components[numberOfComponents]->identifier);
      // fprintf(LOG," %3d connections\n",numberOfConnections);
      //   printf("%4d characters, %3d lines\n",characterNumber,lineNumber);
      //   printf(" %3d components (%s...%s)\n",numberOfComponents,
      //      components[1]->identifier,components[numberOfComponents]->identifier);
      //   printf(" %3d connections\n",numberOfConnections);
      //fclose(LOG);
   }

   /**
    * Load the SDL spec from the specified file.
    * @param file
    * @throws SDLException
    */
   public void load(File file) throws SDLException {
      ReadFile(file);
      ParseCircuit();
   }

   private void ReadFile(String fullFilename) throws SDLException {
      URL resource = this.getClass().getResource(fullFilename);

      if(resource == null) {
         ProcessRuntimeError(ErrorType.ERROR_OPENING_SOURCE_FILE, fullFilename);
      } else {
         try(FileReader reader = new FileReader(resource.getFile())) {
         List<String> sourceLines = IOUtils.readLines(reader);
         sourceLinesIterator = sourceLines.iterator();
         }catch(IOException e) {
            ProcessRuntimeError(ErrorType.ERROR_OPENING_SOURCE_FILE, fullFilename);
         }
      }
   }

   private void ReadFile(File fileToRead) throws SDLException {
      try(FileReader reader = new FileReader(fileToRead)) {
         List<String> sourceLines = IOUtils.readLines(reader);
         sourceLinesIterator = sourceLines.iterator();
      }catch(IOException e) {
         ProcessRuntimeError(ErrorType.ERROR_OPENING_SOURCE_FILE, fileToRead.getAbsolutePath());
      }
  }

    private void ParseCircuit(/*TokenType token,char lexeme[]*/) throws SDLException
    //----------------------------------------------------
    {
        /*
        <circuit>    ::= COMPONENTS
                            <component> { <component> }*
                    [ ALIASES
                            <alias> { <alias> }* ]
                        CONNECTIONS
                            <connection> { <connection> }*
                        END
        */

        TokenType token;
        

        atEOF = false;
        //characterNumber = 0;
        lineNumber = 0;
        //pageNumber = 0;
        GetNextSourceLine();
        GetNextCharacter();
        token = GetNextToken();
        if ( token != TokenType.COMPONENTS ) {
             ProcessSyntaxError(ErrorType.EXPECTING_COMPONENTS);
        }
        token = GetNextToken();
         do {
             token = ParseComponent(token,lexeme);
         } while ( (token != TokenType.ALIASES) && (token != TokenType.CONNECTIONS) && (token != TokenType.EOFX) );
        if ( token == TokenType.ALIASES )
        {
            token = GetNextToken();
            do {
                token = ParseAlias(token,lexeme);
            } while ( (token != TokenType.CONNECTIONS) && (token != TokenType.EOFX) );
        }
        if ( token != TokenType.CONNECTIONS )
            ProcessSyntaxError(ErrorType.EXPECTING_CONNECTIONS);
        token = GetNextToken();
        do
            token = ParseConnection(token,lexeme);
        while ( (token != TokenType.END) && (token != TokenType.EOFX) );
        if ( token != TokenType.END ) {
            ProcessSyntaxError(ErrorType.EXPECTING_END);
        }
    }

    private TokenType ParseConnection(TokenType inputToken,char lexeme[]) throws SDLException
    {
        /*
        <connection> ::= <node> - <node>
        */
        NODE node1 = new NODE();
        NODE node2 = new NODE();
        boolean legalConnection;

        inputToken = ParseNode(inputToken,lexeme,node1);
        if ( inputToken != TokenType.DASH ) {
            ProcessSyntaxError(ErrorType.EXPECTING_DASH);
        }
        inputToken = GetNextToken();
        inputToken = ParseNode(inputToken,lexeme,node2);
        legalConnection = AddConnection(node1,node2);
        if ( !legalConnection ) {
            ProcessSyntaxError(ErrorType.ILLEGAL_CONNECTION);
        }
        return inputToken;
    }

    private boolean AddConnection(NODE node1,NODE node2)
    {
/*
Connection is legal? (Y)es, (R)everse-is-legal, (N)o
          | node2
     node1| IN#  OUT#  gate#(IN)  gate#(OUT) POWER GROUND
----------|----------------------------------------------
       IN#|   N     Y          Y           N     N      N
      OUT#|   R     N          N           R     N      N
 gate#(IN)|   R     N          N           R     R      R
gate#(OUT)|   N     Y          Y           N     N      N
     POWER|   N     N          Y           N     N      N
    GROUND|   N     N          Y           N     N      N
*/
       //char connectionIsLegal[6][6] =
       char[][] connectionIsLegal =
       {
           { 'N', 'Y', 'Y', 'N', 'N', 'N' },
           { 'R', 'N', 'N', 'R', 'N', 'N' },
           { 'R', 'N', 'N', 'R', 'R', 'R' },
           { 'N', 'Y', 'Y', 'N', 'N', 'N' },
           { 'N', 'N', 'Y', 'N', 'N', 'N' },
           { 'N', 'N', 'Y', 'N', 'N', 'N' }
       };

       boolean legalConnection = false;
    
       int row =0;
       int col=0;
    
       if      ( node1.index == Constants.INS )
          row = 0;
       else if ( node1.index == Constants.OUTS )
          row = 1;
       else if ( (node1.index <= numberOfComponents) && (node1.INOrOUT ==  TokenType.IN) )
          row = 2;
       else if ( (node1.index <= numberOfComponents) && (node1.INOrOUT == TokenType.OUT) )
          row = 3;
       else if ( node1.index == Constants.POWERX )
          row = 4;
       else if ( node1.index == Constants.GROUNDX )
          row = 5;
       if      ( node2.index == Constants.INS )
          col = 0;
       else if ( node2.index == Constants.OUTS )
          col = 1;
       else if ( (node2.index <= numberOfComponents) && (node2.INOrOUT ==  TokenType.IN) )
          col = 2;
       else if ( (node2.index <= numberOfComponents) && (node2.INOrOUT == TokenType.OUT) )
          col = 3;
       else if ( node2.index == Constants.POWERX )
          col = 4;
       else if ( node2.index == Constants.GROUNDX )
          col = 5;
       switch ( connectionIsLegal[row][col] )
       {
          case 'N': legalConnection = false;
                    break;
          case 'R': connections[++numberOfConnections] = new CONNECTION(node2,node1);
                    legalConnection = true;
                    break;
          case 'Y': connections[++numberOfConnections] = new CONNECTION(node1,node2);
                    legalConnection = true;
                    break;
       }

       return legalConnection;
    }

    private TokenType ParseComponent(TokenType inputToken,char lexeme[]) throws SDLException
    //----------------------------------------------------
    {
    /*
    <component>  ::= <gate> | <latch> | <buffer>
    <gate>       ::= (( AND | NAND | OR | NOR | XOR )) [ *<integer> ] <identifier>
                   | NOT <identifier>
    <latch>      ::= LATCH <identifier>
    <buffer>     ::= BUFFER <identifier>
    */
       TokenType type = TokenType.UNKNOWN; //setting to UNKNOWN to keep the compiler happy
       boolean found;
       int numberOfINPins =0;
    
       if ( (inputToken ==  TokenType.AND) ||
            (inputToken == TokenType.NAND) ||
            (inputToken ==   TokenType.OR) ||
            (inputToken ==  TokenType.NOR) ||
            (inputToken ==  TokenType.XOR) )
       {
          type = inputToken;
          inputToken = GetNextToken();
          if ( inputToken == TokenType.STAR )
          {
            inputToken = GetNextToken();
             if ( inputToken != TokenType.INTEGER ) {
                ProcessSyntaxError(ErrorType.EXPECTING_INTEGER);
             }
             numberOfINPins = atoi(lexeme);
             if ( (numberOfINPins < 2) || (numberOfINPins > Constants.MAXIMUMINPINS) )
                ProcessSyntaxError(ErrorType.ILLEGAL_NUMBER_OF_INPUT_PINS);
                inputToken = GetNextToken();
          }
          else
             numberOfINPins = 2;
       }
       else if ( inputToken == TokenType.NOT )
       {
          type = inputToken;
          numberOfINPins = 1;
          inputToken = GetNextToken();
       }
       else if ( inputToken == TokenType.LATCHX )
       {
          type = inputToken;
          inputToken = GetNextToken();
       }
       else if ( inputToken == TokenType.BUFFERX )
       {
          type = inputToken;
          inputToken = GetNextToken();
       }
       else {
          ProcessSyntaxError(ErrorType.EXPECTING_COMPONENTTYPE);
       }
    
       if ( inputToken != TokenType.IDENTIFIER ) {
          ProcessSyntaxError(ErrorType.EXPECTING_IDENTIFIER);
       }
       String identifier = asString(lexeme);
       found = FindComponentIdentifier(identifier, new NODE());
       if ( found ) {
          ProcessSyntaxError(ErrorType.MULTIPLY_DEFINED_IDENTIFIER);
       }

       switch ( type )
       {
          case  AND:
          case   OR:
          case NAND:
          case  NOR:
          case  XOR:
          case  NOT: 
             AddGate(identifier,type,numberOfINPins);
             break;
          case LATCHX:
             AddLatch(identifier);
             break;
          case BUFFERX:
             AddBuffer(identifier);
             break;
          //added to keep the compiler happy
          default:
            break;
       }
       return GetNextToken();
    }

    private void AddLatch(String identifier) {
       numberOfComponents++;
       components[numberOfComponents] = new LATCH(identifier);
    }

    private void AddBuffer(String identifier)
    //----------------------------------------------------
    {
       numberOfComponents++;
       components[numberOfComponents] = new BUFFER(identifier);
    }

    private TokenType ParseAlias(TokenType inputToken,char inputLex[]) throws SDLException
    //----------------------------------------------------
    {
    /*
    <alias>      ::= <identifier> = <node>
    */
       String identifier;
       boolean found;
       NODE unusedNode = new NODE();
    
       if ( inputToken != TokenType.IDENTIFIER )
          ProcessSyntaxError(ErrorType.EXPECTING_IDENTIFIER);
       identifier = asString(inputLex);
       if ( FindAliasIdentifier(identifier, unusedNode) )
          ProcessSyntaxError(ErrorType.MULTIPLY_DEFINED_IDENTIFIER);
       found = FindComponentIdentifier(identifier, unusedNode);
       if ( found )
          ProcessSyntaxError(ErrorType.MULTIPLY_DEFINED_IDENTIFIER);
       inputToken = GetNextToken();
       if ( inputToken != TokenType.EQUAL )
          ProcessSyntaxError(ErrorType.EXPECTING_EQUAL);
       inputToken = GetNextToken();

       NODE node = new NODE();
       inputToken = ParseNode(inputToken,inputLex,node);
       AddAlias(identifier,node);
       return inputToken;
    }

    private TokenType ParseNode(TokenType inputToken,char lexeme[], NODE node) throws SDLException
    //----------------------------------------------------
    {
    /*
    <node>       ::= POWER | GROUND 
                   |<identifer>
                   |  IN#<integer> 
                   | OUT#<integer>
                   | <identifer>#<integer>
    */
       boolean found;
    
       switch ( inputToken )
       {
          case IN:
             node.index = Constants.INS;
             node.INOrOUT = TokenType.IN;
             inputToken = GetNextToken();
             if ( inputToken != TokenType.POUND )
                ProcessSyntaxError(ErrorType.EXPECTING_POUND);
             inputToken = GetNextToken();
             if ( inputToken != TokenType.INTEGER )
                ProcessSyntaxError(ErrorType.EXPECTING_INTEGER);
             node.integer = atoi(lexeme);
             if ( (node.integer < 1) || (node.integer > Constants.MAXIMUMINOUTS) )
                ProcessSyntaxError(ErrorType.ILLEGAL_INOUT_INTEGER);
             inputToken = GetNextToken();
             break;
          case OUT:
             node.index = Constants.OUTS;
             node.INOrOUT = TokenType.OUT;
             inputToken = GetNextToken();
             if ( inputToken != TokenType.POUND )
                ProcessSyntaxError(ErrorType.EXPECTING_POUND);
                inputToken = GetNextToken();
             if ( inputToken != TokenType.INTEGER )
                ProcessSyntaxError(ErrorType.EXPECTING_INTEGER);
             node.integer = atoi(lexeme);
             if ( (node.integer < 1) || (node.integer > Constants.MAXIMUMINOUTS) )
                ProcessSyntaxError(ErrorType.ILLEGAL_INOUT_INTEGER);
                inputToken = GetNextToken();
             break;
          case POWER:
             node.index = Constants.POWERX;
             node.INOrOUT = TokenType.OUT;
             node.integer = 0;           // Node field not used.
             inputToken = GetNextToken();
             break;
          case GROUND:
             node.index = Constants.GROUNDX;
             node.INOrOUT = TokenType.OUT;
             node.integer = 0;           // Node field not used.
             inputToken = GetNextToken();
             break;
          case IDENTIFIER:
            String identifier = asString(lexeme);
             if ( FindAliasIdentifier(identifier, node) )
                inputToken = GetNextToken();
             else
             {
                found = FindComponentIdentifier(identifier,node);
                if ( !found )
                   ProcessSyntaxError(ErrorType.UNDEFINED_IDENTIFIER);
                inputToken = GetNextToken();
                if ( inputToken != TokenType.POUND )
                   ProcessSyntaxError(ErrorType.EXPECTING_POUND);
                inputToken = GetNextToken();
                if ( inputToken != TokenType.INTEGER )
                   ProcessSyntaxError(ErrorType.EXPECTING_INTEGER);
                node.integer = atoi(lexeme);
                inputToken = GetNextToken();
                if      ( IsComponentINPin(node.index,node.integer) ) 
                   node.INOrOUT = TokenType.IN;
                else if ( IsComponentOUTPin(node.index,node.integer) ) 
                   node.INOrOUT = TokenType.OUT;
                else
                   ProcessSyntaxError(ErrorType.EXPECTING_NODE);
             }
             break;
          default:
             ProcessSyntaxError(ErrorType.EXPECTING_NODE);
       }

       return inputToken;
    }

    private int atoi(char[] array) {
        return Integer.valueOf(asString(array));
    }

    //----------------------------------------------------
    private boolean IsComponentINPin(int index,int integer)
    //----------------------------------------------------
    {
    return( components[index].IsINPin(integer) );
    }

    //----------------------------------------------------
    private boolean IsComponentOUTPin(int index,int integer)
    //----------------------------------------------------
    {
    return( components[index].IsOUTPin(integer) );
    }

    private void AddAlias(String identifier,NODE node)
    //----------------------------------------------------
    {
       numberOfAliases++;
       aliases[numberOfAliases] = new ALIAS(identifier, node);
    }

    private boolean FindAliasIdentifier(String identifier, NODE node)
    //----------------------------------------------------
    {
       //NODE node = null;
        //char uIdentifier1[IDENTIFIERLENGTH+1];

       boolean found = false;
       int index = 1;
       //for (i = 0; i <= (int) strlen(identifier); i++)
       //   uIdentifier1[i] = toupper(identifier[i]);
       
       while ( (index <= numberOfAliases) && !found )
       {
          //char uIdentifier2[IDENTIFIERLENGTH+1];
    
          //for (i = 0; i <= (int) strlen(aliases[index].identifier); i++)
          //   uIdentifier2[i] = toupper(aliases[index].identifier[i]);
          if ( StringUtils.equalsAnyIgnoreCase(identifier, aliases[index].getIdentifier()) )
          {
             NODE foundNode = aliases[index].getNode();
             node.index = foundNode.index;
             node.INOrOUT = foundNode.INOrOUT;
             node.integer = foundNode.integer;

             found = true;
          }
          else
             index++;
       }

       return found;
    }

    private String asString(char[] chars) {
        int index = ArrayUtils.indexOf(chars, '\0', 0);

        if(index < 1) {
            return String.valueOf(chars);
        } else {
            return String.valueOf(ArrayUtils.subarray(chars, 0, index));
        }
    }

    //NODE can be null
    private boolean FindComponentIdentifier(String identifier, NODE node)
    //----------------------------------------------------
    {
       boolean found = false;
       int index = 1;

       while ( (index <= numberOfComponents) && !found )
       {
          if(StringUtils.equalsAnyIgnoreCase(identifier, components[index].getIdentifier())) {
              found = true;
          } else {
              index++;
          }
       }

       //workaround for C++ code passing in the index by ref
       node.index = index;

       return found;
    }

    private void AddGate(String identifier,TokenType type,int numberOfINPins)
    //----------------------------------------------------
    {
        numberOfComponents++;
        switch ( type )
        {
            case  AND: components[numberOfComponents] = new  ANDGATE(identifier,numberOfINPins);
                        break;
            case NAND: components[numberOfComponents] = new NANDGATE(identifier,numberOfINPins);
                        break;
            case   OR: components[numberOfComponents] = new   ORGATE(identifier,numberOfINPins);
                        break;
            case  NOR: components[numberOfComponents] = new  NORGATE(identifier,numberOfINPins);
                        break;
            case  XOR: components[numberOfComponents] = new  XORGATE(identifier,numberOfINPins);
                        break;
            case  NOT: components[numberOfComponents] = new  NOTGATE(identifier);
                        break;
            default: //added to keep the compiler happy
                        break;
        }
    }

    private TokenType GetNextToken() {
    /*
       <letter>    ::= A | B | ... | Z | a | b | ... | z
       <digit>     ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
       <character> ::= Any printable ASCII character
    */
       int j;

       TokenType token;
    
       do
       {
          /*
             "Eat" any whitespace (blanks and EOLs and TABs).
          */
          while ( (nextCharacter == ' ')
               || (nextCharacter == Constants.EOL)
               || (nextCharacter == '\t') ) {
             GetNextCharacter();
            }
    
          /*
             "Eat" any comments. Comments are always assumed to extend to EOL.
             <comment> ::= $ {<character>}*
          */
          if ( nextCharacter == '$' )
             do
                GetNextCharacter();
             while ( nextCharacter != Constants.EOL );
       } while ( (nextCharacter == ' ')
              || (nextCharacter == Constants.EOL)
              || (nextCharacter == '\t') );
    /*
       Reserved words and
       <identifier> ::= <letter> { (( <letter> | <digit> )) }*
    */
       if ( CharUtils.isAsciiAlpha(nextCharacter) )
       {
          j = 0;
          lexeme[j++] = nextCharacter;
          GetNextCharacter();
          while ( CharUtils.isAsciiAlpha(nextCharacter) || CharUtils.isAsciiNumeric(nextCharacter) )
          {
             lexeme[j++] = nextCharacter;
             GetNextCharacter();
          }
          lexeme[j] = '\0';
        //   for (k = 0; k <= (int) strlen(lexeme); k++) {
        //      uLexeme[k] = toupper(lexeme[k]);
        //   }
          String upperLex = StringUtils.upperCase(asString(lexeme));

          switch (upperLex) {
              case "COMPONENTS":
                token = TokenType.COMPONENTS;
                break;
            case "ALIASES":
                token = TokenType.ALIASES;
                break;
            case "CONNECTIONS":
                token = TokenType.CONNECTIONS;
                break;
                case "END":
                token = TokenType.END;
                break;
                case "AND":
                token = TokenType.AND;
                break;
                case "NAND":
                token = TokenType.NAND;
                break;
                case "OR":
                token = TokenType.OR;
                break;
                case "NOR":
                token = TokenType.NOR;
                break;
                case "XOR":
                token = TokenType.XOR;
                break;
                case "NOT":
                token = TokenType.NOT;
                break;
                case "LATCH":
                token = TokenType.LATCHX;
                break;
                case "BUFFER":
                token = TokenType.BUFFERX;
                break;
                case "IN":
                token = TokenType.IN;
                break;
                case "OUT":
                token = TokenType.OUT;
                break;
                case "POWER":
                token = TokenType.POWER;
                break;
                case "GROUND":
                token = TokenType.GROUND;
                break;
              default:
                token = TokenType.IDENTIFIER;
                break;
          }
       }
    /*
       <integer> ::= <digit> { <digit> }*
    */
       else if ( CharUtils.isAsciiNumeric(nextCharacter) )
       {
          token = TokenType.INTEGER;
          j = 0;
          lexeme[j++] = nextCharacter;
          GetNextCharacter();
          while ( CharUtils.isAsciiNumeric(nextCharacter) )
          {
             lexeme[j++] = nextCharacter;
             GetNextCharacter();
          }
          lexeme[j] = '\0';
       }
       else
       {
          switch ( nextCharacter )
          {
             case Constants.EOF: token = TokenType.EOFX;
                       lexeme[0] = '\0';
                       break;
             case '*': token = TokenType.STAR;
                       lexeme[0] = nextCharacter; lexeme[1] = '\0';
                       GetNextCharacter();
                       break;
             case '#': token = TokenType.POUND;
                       lexeme[0] = nextCharacter; lexeme[1] = '\0';
                       GetNextCharacter();
                       break;
             case '-': token = TokenType.DASH;
                       lexeme[0] = nextCharacter; lexeme[1] = '\0';
                       GetNextCharacter();
                       break;
             case '=': token = TokenType.EQUAL;
                       lexeme[0] = nextCharacter; lexeme[1] = '\0';
                       GetNextCharacter();
                       break;
             default:  token = TokenType.UNKNOWN;
                       lexeme[0] = nextCharacter; lexeme[1] = '\0';
                       GetNextCharacter();
                       break;
          }
       }

       return token;
    }

    private void GetNextSourceLine() {
       if(!sourceLinesIterator.hasNext()) {
           atEOF = true;
       } else {
            String nextLine = StringUtils.truncate(sourceLinesIterator.next(), Constants.LINELENGTH);
            sourceLine = nextLine.toCharArray();

            atEOF = false;
            atEOL = false;
        /*
        Erase control characters at end of source line (if any)
        */
        for(int i = 0; i < sourceLine.length; i++) {
            if(CharUtils.isAsciiControl(sourceLine[i])) {
                sourceLine[i] = '\0';
            }
        }
        // while ( (0 <= (int) strlen(sourceLine)-1) && 
        //     iscntrl(sourceLine[strlen(sourceLine)-1]) ) {
        //     sourceLine[strlen(sourceLine)-1] = '\0';
        // }
        sourceLineIndex = 0;
        /*
        Echo source line to LOG file using the format
            for source line number and text, respectively.
        
                111111
        123456789012345
        Page XXXX
        
        Line Source Line
        ---- ----------------------------------------------------------------------
        XXXX XXX---source line text---XXX
        .   .
        .   .
        XXXX XXX---source line text---XXX
        */
          lineNumber++;
          if ( lineNumber % 50 == 1 )
          {
             //pageNumber++;
             //final char FF = 0X0C;
            //  fprintf(LOG,"%cPage %4d\n",FF,pageNumber);
            //  fprintf(LOG,"\n");
            //  fprintf(LOG,"Line Source Line\n");
            //  fprintf(LOG,"---- ----------------------------------------------------------------------\n");
          }
        //   fprintf(LOG,"%4d %s\n",lineNumber,sourceLine);
       }
    }

    private void GetNextCharacter() {
       if ( atEOF ) {
          nextCharacter = Constants.EOF;
       } else {
          if ( atEOL ) {
             GetNextSourceLine();
          }
          if ( sourceLineIndex <= ((int) strlen(sourceLine)-1) )
          {
             nextCharacter = sourceLine[sourceLineIndex];
             sourceLineIndex += 1;
          }
          else
          {
             nextCharacter = Constants.EOL;
             atEOL = true;
          }
       }
       //characterNumber++;
    }

    private int strlen(char[] array) {
        int i = 0;
        for(; i < array.length; i++) {
            if(array[i] == '\0') {
                break;
            }
        }

        return i;
    }

    private void ProcessRuntimeError(ErrorType error,String additionalInformation) throws SDLException
    //----------------------------------------------------
    {
        String message = error.name();

        if(StringUtils.isNotEmpty(additionalInformation)) {
            message = String.format("%s[%s]", error.name(), additionalInformation);
        }
        
        throw new SDLException(message);
    }

    private void ProcessSyntaxError(ErrorType error) throws SDLException
    //----------------------------------------------------
    {
        /*
        Use panic mode error recovery technique; viz., log error message,
        close source and LOG files and throw exception.
        */
        //fprintf(LOG,"     ");
        //for (int i = 1; i <= (sourceLineIndex-1); i++)
        //    fprintf(LOG," ");
        //fprintf(LOG,"^ %s\n",&errorMessages[error][2]);
        //fclose(SOURCE);
        //fclose(LOG);
        throw new SDLException(error.name());
    }

    public void SimulateLogic() throws SDLException
    //----------------------------------------------------
    {
       int i;
    
    // Assumes that all SetIN()s have made by circuit owner.
    // Set all OUT#s to UNK
       for (i = 1; i <= Constants.MAXIMUMINOUTS; i++)
          OUTs[i] = StateType.UNK;
    // Set all gate pins to UNK
       for (i = 1; i <= numberOfComponents; i++)
          components[i].SetINPinsToUNK();
    // Process connections
    //    * POWER/GROUND - component#(IN)
       for (i = 1; i <= numberOfConnections; i++)
       {
          NODE n1 = connections[i].getNode1();
          NODE n2 = connections[i].getNode2();
    
          if ( (n1.index == Constants.POWERX) &&
               (n2.index <= numberOfComponents) &&
               (n2.INOrOUT == TokenType.IN) )
             components[n2.index].SetINPin(n2.integer,StateType.ON);
          if ( (n1.index == Constants.GROUNDX) &&
               (n2.index <= numberOfComponents) &&
               (n2.INOrOUT == TokenType.IN) )
             components[n2.index].SetINPin(n2.integer,StateType.OFF);
       }
    //    * IN# - OUT#
       for (i = 1; i <= numberOfConnections; i++)
       {
          NODE n1 = connections[i].getNode1();
          NODE n2 = connections[i].getNode2();
    
          if ( (n1.index == Constants.INS) &&
               (n2.index == Constants.OUTS) )
             OUTs[n2.integer] = GetIN(n1.integer);
       }
    //    * IN# - component#(IN)
       for (i = 1; i <= numberOfConnections; i++)
       {
          NODE n1 = connections[i].getNode1();
          NODE n2 = connections[i].getNode2();
    
          if ( (n1.index == Constants.INS) &&
               (n2.index <= numberOfComponents) &&
               (n2.INOrOUT == TokenType.IN) )
             components[n2.index].SetINPin(n2.integer,GetIN(n1.integer));
       }
    //    * component#(OUT) - component#(IN)
       for (int g = 1; g <= numberOfComponents; g++)
          for (i = 1; i <= numberOfConnections; i++)
          {
             NODE n1 = connections[i].getNode1();
             NODE n2 = connections[i].getNode2();
    
             if ( (n1.index <= numberOfComponents) &&
                  (n1.INOrOUT == TokenType.OUT) &&
                  (n2.index <= numberOfComponents) &&
                  (n2.INOrOUT == TokenType.IN) )
                components[n2.index].SetINPin(n2.integer,components[n1.index].GetPin(n1.integer));
          }
    //    * component#(OUT) - OUT#
       for (i = 1; i <= numberOfConnections; i++)
       {
          NODE n1 = connections[i].getNode1();
          NODE n2 = connections[i].getNode2();
    
          if ( (n1.index <= numberOfComponents) &&
               (n1.INOrOUT == TokenType.OUT) &&
               (n2.index == Constants.OUTS) )
             OUTs[n2.integer] = components[n1.index].GetPin(n1.integer);
       }
    }

    private StateType GetIN(int integer) throws SDLException
    {
        if ( (integer < 1) || (integer > Constants.MAXIMUMINOUTS) )
        {
            ProcessRuntimeError(ErrorType.ILLEGAL_INOUT_INTEGER,String.valueOf(integer));
        }
        return(  INs[integer] );
    }
}