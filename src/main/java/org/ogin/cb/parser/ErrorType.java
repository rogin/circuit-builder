package org.ogin.cb.parser;

public enum ErrorType {
    // Syntax Errors
   EXPECTING_COMPONENTS         ,//,//=  1,
   EXPECTING_CONNECTIONS        ,//=  2,
   EXPECTING_END                ,//=  3,
   EXPECTING_COMPONENTTYPE      ,//=  4,
   EXPECTING_IDENTIFIER         ,//=  5,
   MULTIPLY_DEFINED_IDENTIFIER  ,//=  6,
   EXPECTING_POUND              ,//=  7,
   EXPECTING_INTEGER            ,//=  8,
   ILLEGAL_COMPONENT_PIN        ,//=  9,
   EXPECTING_NODE               ,//= 10,
   UNDEFINED_IDENTIFIER         ,//= 11,
   EXPECTING_INOROUT            ,//= 12,
   EXPECTING_EQUAL              ,//= 13,
   ILLEGAL_CONNECTION           ,//= 14,
   ILLEGAL_NUMBER_OF_INPUT_PINS ,//= 15,
   EXPECTING_DASH               ,//= 16,
// Runtime Errors
   ERROR_OPENING_SOURCE_FILE    ,//= 20,
   ERROR_OPENING_LOG_FILE       ,//= 21,
   ILLEGAL_INOUT_INTEGER        ,//= 22,
   ILLEGAL_INOUT_ALIAS          ,//= 23,
   ERROR_OPENING_TESTS_FILE     ,//= 24,
   NOTUSED;
}