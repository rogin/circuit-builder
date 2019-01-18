package org.ogin.cb.models;

public class BUFFER extends COMPONENT{

    public BUFFER(String identifier) {
      super(identifier,TokenType.BUFFERX,2);
    }

/*
 Pin#1   Pin#2   Pin#3
DataIn Control DataOut
   ON      ON      ON
   OFF     ON      OFF
   Any     OFF     UNK
*/
   public StateType GetPin(int integer)
   {
    StateType r;

      if      ( integer <= numberOfINPins )
         r = pins[integer];
      else if ( integer == (numberOfINPins+1) )
         if      ( (pins[1] ==  StateType.ON) &&  (pins[2] ==  StateType.ON) )
            r =  StateType.ON;
         else if ( (pins[1] == StateType.OFF) &&  (pins[2] ==  StateType.ON) )
            r = StateType.OFF;
         else if ( (pins[1] != StateType.UNK) &&  (pins[2] == StateType.OFF) )
            r = StateType.UNK;
         else
            r = StateType.UNK;
      else
         r = StateType.UNK;
      return( r );
   }
}