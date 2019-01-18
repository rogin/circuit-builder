package org.ogin.cb.models;

public abstract class GATE extends COMPONENT{

    public GATE(String identifier,TokenType type) {
      this(identifier,type,1);
    }

    public GATE(String identifier,TokenType type,int numberOfINPins) {
        super(identifier,type,numberOfINPins);
      }

/*
  a    b  a AND b  a NAND b  a OR b  a NOR b  a XOR b  NOT a
OFF  OFF      OFF       ON      OFF      ON      OFF     ON
OFF  ON       OFF       ON      ON       OFF     ON      ON
ON   OFF      OFF       ON      ON       OFF     ON      OFF
ON   ON       ON        OFF     ON       OFF     OFF     OFF
ON   UNK      UNK       UNK     ON       OFF     UNK     OFF
OFF  UNK      OFF       ON      UNK      UNK     UNK     ON
UNK  ON       UNK       UNK     ON       OFF     UNK     UNK
UNK  OFF      OFF       ON      UNK      UNK     UNK     UNK

Inherited from parent class COMPONENT
   virtual STATE GetPin(int integer) = 0;
*/
   boolean AllON()
   {
      boolean r = true;

      for (int i = 1; i <= numberOfINPins; i++)
         r = r && (pins[i] == StateType.ON);
      return( r );
   }

   boolean AllOFF()
   {
      boolean r = true;

      for (int i = 1; i <= numberOfINPins; i++)
         r = r && (pins[i] ==  StateType.OFF);
      return( r );
   }

   boolean AnyON()
   {
      boolean r = false;

      for (int i = 1; i <= numberOfINPins; i++)
         r = r || (pins[i] ==  StateType.ON);
      return( r );
   }

   boolean AnyOFF()
   {
      boolean r = false;

      for (int i = 1; i <= numberOfINPins; i++)
         r = r || (pins[i] ==  StateType.OFF);
      return( r );
   }
}