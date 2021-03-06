/* Generated by AN DISI Unibo */ 
package it.unibo.clock

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Clock ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("clock | started")
					}
					 transition( edgeName="goto",targetState="morning", cond=doswitch() )
				}	 
				state("morning") { //this:State
					action { //it:State
						emit("switch", "switch(morning)" ) 
						println("clock | morning started")
						delay(8000) 
						println("clock | morning finished")
					}
					 transition( edgeName="goto",targetState="afternoon", cond=doswitch() )
				}	 
				state("afternoon") { //this:State
					action { //it:State
						emit("switch", "switch(afternoon)" ) 
						println("clock | afternoon started")
						delay(8000) 
						println("clock | afternoon ended")
					}
					 transition( edgeName="goto",targetState="night", cond=doswitch() )
				}	 
				state("night") { //this:State
					action { //it:State
						emit("switch", "switch(night)" ) 
						println("clock | night started")
						delay(8000) 
						println("clock | night ended")
					}
					 transition( edgeName="goto",targetState="morning", cond=doswitch() )
				}	 
			}
		}
}
