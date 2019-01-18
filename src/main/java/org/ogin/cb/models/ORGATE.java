package org.ogin.cb.models;

public class ORGATE extends GATE {

   public ORGATE(String identifier,int numberOfINPins) {
    super(identifier,TokenType.OR,numberOfINPins);
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
            r = AnyON() ? StateType.ON : StateType.OFF;
      else
         r = StateType.UNK;
      return( r );
   }
}