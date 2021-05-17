%====================================================================================
% es3 description   
%====================================================================================
context(ctxservice, "localhost",  "TCP", "8076").
 qactor( serviceactor, ctxservice, "it.unibo.serviceactor.Serviceactor").
  qactor( clientactor, ctxservice, "it.unibo.clientactor.Clientactor").
msglogging.
