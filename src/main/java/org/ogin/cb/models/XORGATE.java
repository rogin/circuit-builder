package org.ogin.cb.models;

public class XORGATE extends GATE{

    public XORGATE(String identifier,int numberOfINPins) {
        super(identifier,TokenType.XOR,numberOfINPins);
    }

    public StateType GetPin(int integer)
    {
        StateType r;
 
       if      ( integer <= numberOfINPins )
          r = pins[integer];
       else if ( integer == (numberOfINPins+1) )
          if ( AnyUNK() )
             r = StateType.UNK;
          else
          {
          /*
             An XOR with 2 or more inputs outputs a ON when the number of ONs 
             at its inputs is odd, and a OFF when the number of incoming ONs 
             is even.
          */
             int c = 0;
             
             for (int i = 1; i <= numberOfINPins; i++)
                if ( pins[i] == StateType.ON ) c++;
             r = (c%2 == 0) ? StateType.OFF : StateType.ON;
          }
       else
          r = StateType.UNK;
       return( r );
    }
}