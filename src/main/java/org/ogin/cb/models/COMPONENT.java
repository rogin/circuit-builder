package org.ogin.cb.models;

import org.ogin.cb.Constants;

public abstract class COMPONENT {

    protected String identifier;
    protected TokenType type;                    // AND,NAND,OR,NOR,XOR,NOT,LATCHX,BUFFERX
    protected int numberOfINPins;
    protected StateType[] pins = new StateType[Constants.MAXIMUMINPINS+1];   // pins[1..numberOfINPins] are input pins (pins[0] is ignored)
    // pins[numberOfINPins+1] virtual (i.e., computed) output pin

   public COMPONENT(String identifier,TokenType type,int numberOfINPins)
   {
      this.identifier = identifier;
      this.type = type;
      this.numberOfINPins = numberOfINPins;
   }

   public String getIdentifier() {
      return identifier;
   }

   public TokenType GetType()
   {
      return( type );
   }

   public void SetINPinsToUNK()
   {
      for (int i = 1; i <= numberOfINPins; i++)
         pins[i] = StateType.UNK;
   }

   public void SetINPin(int integer,StateType state)
   {
      if ( integer <= numberOfINPins )
         pins[integer] = state;
   }

   public boolean IsINPin(int integer)
   {
      return ( integer <= numberOfINPins );
   }

   public boolean IsOUTPin(int integer)
   {
      return ( integer == (numberOfINPins+1) );
   }

   public boolean AnyUNK()
   {
    boolean r = false;

      for (int i = 1; i <= numberOfINPins; i++)
         r = r || (pins[i] == StateType.UNK);
      return( r );
   }

   public abstract StateType GetPin(int integer);
}