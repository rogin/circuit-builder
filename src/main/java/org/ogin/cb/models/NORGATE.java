package org.ogin.cb.models;

public class NORGATE extends GATE {

    public NORGATE(String identifier,int numberOfINPins) {
        super(identifier,TokenType.NOR,numberOfINPins);
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
             r = AnyON() ? StateType.OFF : StateType.ON;
       else
          r = StateType.UNK;
       return( r );
    }
}