package it.unibo.robotWithActorJava;

import it.unibo.supports2021.ActorBasicJava;
import it.unibo.supports2021.IssWsHttpJavaSupport;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ResumableBoundaryWalkerActor extends ActorBasicJava {
    final String forwardMsg   = "{\"robotmove\":\"moveForward\", \"time\": 350}";
    final String backwardMsg  = "{\"robotmove\":\"moveBackward\", \"time\": 350}";
    final String turnLeftMsg  = "{\"robotmove\":\"turnLeft\", \"time\": 300}";
    final String turnRightMsg = "{\"robotmove\":\"turnRight\", \"time\": 300}";
    final String haltMsg      = "{\"robotmove\":\"alarm\", \"time\": 100}";

    private enum State {pause, walking, obstacle, halted };
    private IssWsHttpJavaSupport support;
    private State curState       =  State.pause ;
    private int stepNum          = 0;
    private RobotMovesInfo moves = new RobotMovesInfo(true);

    private String savedMove;
    private String savedEndmove;
    private State savedState;

    public ResumableBoundaryWalkerActor(String name, IssWsHttpJavaSupport support) {
        super(name);
        this.support = support;
    }
/*
//Removed since we want use just the fsm, without any 'external' code
    public void reset(){
        System.out.println("RobotBoundaryLogic | FINAL MAP:"  );
        moves.showRobotMovesRepresentation();
        stepNum        = 1;
        curState       =  State.start;
        moves.getMovesRepresentationAndClean();
        moves.showRobotMovesRepresentation();
    }
*/

    protected void fsm(String x, String y){
        System.out.println( myname + " | fsm state=" + curState + " stepNum=" + stepNum + " move=" + x + " endmove=" + y);
        switch( curState ) {
            case pause: {
                if(x.equals("RESUME") && y.equals("robotcmd")) {
                    stepNum = 1;
                    doStep();
                    curState = State.walking;
                }
                break;
            }
            case walking: {
                if (x.equals("moveForward") && y.equals("true")) {
                    //curState = State.walk;
                    moves.updateMovesRep("w");
                    doStep();
                 } else if (x.equals("moveForward") && y.equals("false")) {
                    curState = State.obstacle;
                    turnLeft();
                } else if(x.equals("STOP") && y.equals("robotcmd")) {
                    this.savedState = this.curState;
                    this.curState = State.halted;
                }

                moves.showRobotMovesRepresentation();

                /*else {System.out.println("IGNORE answer of turnLeft");
                }*/
                break;
            }//walk

            case obstacle :
                if( x.equals("turnLeft") && y.equals("true")) {
                    moves.updateMovesRep("l");
                    moves.showRobotMovesRepresentation();
                    if( stepNum < 4) {
                        stepNum++;
                        curState = State.walking;
                        doStep();
                    }else{  //at home again
                        this.curState = State.pause;
                        moves.getMovesRepresentationAndClean();
                        //turnLeft(); //to force state transition
                    }
                } else if (x.equals("STOP") && y.equals("robotcmd")) {
                    this.savedState = this.curState;
                    this.curState = State.halted;
                }
                break;

            case halted : {
                if(x.equals("RESUME") && y.equals("robotcmd")) {
                        this.curState = this.savedState;
                        resumeByHalt();
                } else if(y.equals("true")) {
                   saveMsg(x, y);
                }
                break;
            }
            default: {
                System.out.println("error - curState = " + curState);
            }
        }
    }


    @Override
    protected void handleInput(String msg ) {     //called when a msg is in the queue
        //System.out.println( name + " | input=" + msgJsonStr);
        //if( msg.equals("startApp"))  fsm("","");
        /*else*/ msgDriven( new JSONObject(msg) );
    }

    protected void msgDriven( JSONObject infoJson){
        if( infoJson.has("endmove") )        fsm(infoJson.getString("move"), infoJson.getString("endmove"));
        else if( infoJson.has("robotcmd"))   fsm(infoJson.getString("robotcmd"), "robotcmd");
        else if( infoJson.has("sonarName") ) handleSonar(infoJson);
        else if( infoJson.has("collision") ) handleCollision(infoJson);
        else if( infoJson.has("robotcmd") )  handleRobotCmd(infoJson);
    }

    protected void handleSonar( JSONObject sonarinfo ){
        String sonarname = (String)  sonarinfo.get("sonarName");
        int distance     = (Integer) sonarinfo.get("distance");
        //System.out.println("RobotApplication | handleSonar:" + sonarname + " distance=" + distance);
    }
    protected void handleCollision( JSONObject collisioninfo ){
        //we should handle a collision  when there are moving obstacles
        //in this case we could have a collision even if the robot does not move
        //String move   = (String) collisioninfo.get("move");
        //System.out.println("RobotApplication | handleCollision move=" + move  );
    }
  
    protected void handleRobotCmd( JSONObject robotCmd ){
        String cmd = (String)  robotCmd.get("robotcmd");
        System.out.println("===================================================="    );
        System.out.println("RobotApplication | handleRobotCmd cmd=" + cmd  );
        System.out.println("===================================================="    );
    }

    //------------------------------------------------
    protected void doStep(){
        support.forward( forwardMsg);
        delay(1000); //to avoid too-rapid movement
    }
    protected void turnLeft(){
        support.forward( turnLeftMsg );
        delay(500); //to avoid too-rapid movement
    }
    protected void turnRight(){
        support.forward( turnRightMsg );
        delay(500); //to avoid too-rapid movement
    }

    protected void resumeByHalt() {
        String savedMove, savedEndMove;
        if(this.savedMove != null && this.savedEndmove != null) {
            savedMove = this.savedMove; savedEndMove = this.savedEndmove;
            this.savedMove = null; this.savedEndmove = null;
            System.out.println("robotAppl | resumeByHealy() savedMove = " + savedMove + " savedEndMode = " + savedEndMove);
            fsm(savedMove, savedEndMove);
        }

    }

    protected void saveMsg(String x, String y) {
        this.savedMove = x; this.savedEndmove = y;
    }

    protected void reset() {
        this.stepNum = 1;
        fsm("", "");
    }

}
