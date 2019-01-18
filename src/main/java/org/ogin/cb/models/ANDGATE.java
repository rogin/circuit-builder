package org.ogin.cb.models;

public class ANDGATE extends GATE{

   public ANDGATE(String identifier,int numberOfINPins) {
    super(identifier,TokenType.AND,numberOfINPins);
   }

   @Override
   public StateType GetPin(int integer)
   {
    StateType r;

      if      ( integer <= numberOfINPins )
         r = pins[integer];
      else if ( integer == (numberOfINPins+1) )
         if ( AnyUNK() )
            r = StateType.UNK;
         else
            r = AllON() ? StateType.ON : StateType.OFF;
      else
         r = StateType.UNK;
      return( r );
   }
}