package org.ogin.cb.models;

public class NANDGATE extends GATE {

   public NANDGATE(String identifier,int numberOfINPins) {
      super(identifier,TokenType.NAND,numberOfINPins);
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
            r = AllON() ? StateType.OFF : StateType.ON;
      else
         r = StateType.UNK;
      return( r );
   }
}