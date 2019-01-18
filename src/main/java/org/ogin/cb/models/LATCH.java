package org.ogin.cb.models;

public class LATCH extends COMPONENT{

    protected StateType state;

    public LATCH(String identifier) {
      super(identifier,TokenType.LATCHX,4);
      state = StateType.OFF;
   }

    /*
    Pin#1  Pin#2  Pin#3  Pin#4             Pin#5
        D     CK    Set  Clear  State Changes To
    Any    Any    ON     OFF       ON
    Any    Any    OFF    ON        OFF
    ON     ON     OFF    OFF       ON
    OFF    ON     OFF    OFF       OFF
    Any    OFF    OFF    OFF       Not changed
    Any    Any    ON     ON        Not changed
    
    Any means either ON or OFF
    */
   public StateType GetPin(int integer)
   {
    StateType r;

      if      ( integer <= numberOfINPins )
         r = pins[integer];
      else if ( integer == (numberOfINPins+1) )
      {
         if ( AnyUNK() )
            r = StateType.UNK;
         else
         {
            if      ( (pins[1] != StateType.UNK) &&  (pins[2] != StateType.UNK) &&  (pins[3] ==  StateType.ON) && (pins[4] == StateType.OFF) )
               state =  StateType.ON;
            else if ( (pins[1] != StateType.UNK) &&  (pins[2] != StateType.UNK) &&  (pins[3] == StateType.OFF) && (pins[4] ==  StateType.ON) )
               state = StateType.OFF;
            else if ( (pins[1] ==  StateType.ON) &&  (pins[2] ==  StateType.ON) &&  (pins[3] == StateType.OFF) && (pins[4] == StateType.OFF) )
               state =  StateType.ON;
            else if ( (pins[1] == StateType.OFF) &&  (pins[2] ==  StateType.ON) &&  (pins[3] == StateType.OFF) && (pins[4] == StateType.OFF) )
               state = StateType.OFF;
            else if ( (pins[1] != StateType.UNK) &&  (pins[2] == StateType.OFF) &&  (pins[3] == StateType.OFF) && (pins[4] == StateType.OFF) )
               /* State not changed*/;
            else/*if ( (pins[1] != StateType.UNK) &&  (pins[2] != StateType.UNK) &&  (pins[3] ==  ON) && (pins[4] ==  ON) )*/
               /* State not changed*/;
            r = state;
         }
      }
      else
         r = StateType.UNK;
      return( r );
   }
}