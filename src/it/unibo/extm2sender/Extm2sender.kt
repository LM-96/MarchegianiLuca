/* Generated by AN DISI Unibo */ 
package it.unibo.extm2sender

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Extm2sender ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						 delay((Math.random() * 5000).toLong())  
						forward("m2", "m2(m2)" ,"workshiftactor" ) 
						println("extm2sender | sent m2 message to workshiftactor")
					}
					 transition( edgeName="goto",targetState="s0", cond=doswitch() )
				}	 
			}
		}
}
