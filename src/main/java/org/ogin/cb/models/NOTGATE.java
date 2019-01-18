package org.ogin.cb.models;

public class NOTGATE extends GATE{

    public NOTGATE(String identifier) {
        super(identifier,TokenType.NOT);
    }

    public StateType GetPin(int integer)
    {
        StateType r;
 
       if      ( integer == 1 )
          r = pins[integer];
       else if ( integer == 2 )
          if ( pins[1] == StateType.UNK )
             r = StateType.UNK;
          else
             r = (pins[1] == StateType.ON) ? StateType.OFF : StateType.ON;
       else
          r = StateType.UNK;
       return( r );
    }
}