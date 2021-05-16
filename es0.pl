%====================================================================================
% es0 description   
%====================================================================================
context(ctxworkshift, "localhost",  "TCP", "8048").
context(ctxsenders, "localhost",  "TCP", "8049").
 qactor( workshiftactor, ctxworkshift, "it.unibo.workshiftactor.Workshiftactor").
  qactor( clock, ctxworkshift, "it.unibo.clock.Clock").
  qactor( extm1sender, ctxsenders, "it.unibo.extm1sender.Extm1sender").
  qactor( extm2sender, ctxsenders, "it.unibo.extm2sender.Extm2sender").
