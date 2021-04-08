package it.unibo.cautiousExplorerActors;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Stopwatch;

import it.cautiousExplorerActors.history.JourneyReverter;
import it.cautiousExplorerActors.history.MovementHistory;
import it.unibo.issRobotSpeaker.CommandFactory;
import it.unibo.issRobotSpeaker.Move;
import it.unibo.issRobotSpeaker.MoveResult;
import it.unibo.issRobotSpeaker.RobotCommand;
import it.unibo.issRobotSpeaker.RobotInformation;
import it.unibo.issRobotSpeaker.RobotMovement;
import it.unibo.issRobotSpeaker.RobotSpeaker;
import it.unibo.issRobotSpeaker.SensorData;
import it.unibo.issRobotSpeaker.SonarData;
import it.unibo.supports2021.ActorBasicJava;
import it.unibo.supports2021.IssWsHttpJavaSupport;

public class CautiousExplorerActor extends ActorBasicJava {
	
	private static Map<Move, String> mapMoves = new HashMap<>();
	static {
		mapMoves.put(Move.MOVE_FORWARD, "w");
		mapMoves.put(Move.MOVE_BACKWARD, "s");
		mapMoves.put(Move.TURN_LEFT, "l");
		mapMoves.put(Move.TURN_RIGHT, "r");
	}
	
	private final int timeUnit = 100;
	private final int turnTimeUnit = 300;
	private final int haltTimeUnit = 100;
	
	private final String fw;
	private final String bw;
	private final String tl;
	private final String tr;
	private final String ht;
	
	
	private IssWsHttpJavaSupport support;
	private RobotSpeaker speaker;
	
	private RobotMovesInfo moves;
	private MovementHistory history;
	private boolean blockMap;
	private JourneyReverter reverter;
	
	private int wave;
	private int step;
	private int side;
	private boolean obstacleFound;
	private int counter;
	private Stopwatch stopwatch;
	
	private long timeError;
	
	public CautiousExplorerActor(String name, IssWsHttpJavaSupport support, RobotSpeaker speaker) {
		super(name);
		
		this.speaker = speaker;
		this.support = support;
		
		this.fw = speaker.encode(CommandFactory.createCommand(Move.MOVE_FORWARD, timeUnit));
		this.bw = speaker.encode(CommandFactory.createCommand(Move.MOVE_BACKWARD, timeUnit));
		this.tl = speaker.encode(CommandFactory.createCommand(Move.TURN_LEFT, turnTimeUnit));
		this.tr = speaker.encode(CommandFactory.createCommand(Move.TURN_RIGHT, turnTimeUnit));
		this.ht = speaker.encode(CommandFactory.createCommand(Move.ALARM, haltTimeUnit));
		
		moves = new RobotMovesInfo(true);
		history = new MovementHistory();
		blockMap = false;
		reverter = new JourneyReverter();
		
		this.stopwatch = Stopwatch.createUnstarted();
		
		this.timeError = 0;
		
	}
	
	private void startExploring() {
		wave = 1;
		step = 0;
		side = 1;
		
		sendForward();
	}
	
	private void sendForward() {
		support.forward(fw);
		
		stopwatch.reset();
		stopwatch.start();
	}
	
	private void doWaveStep() {
		//Step corrente (appena eseguito)
		step++;
		System.out.println( this.myname + " | doWaveStep, step=" + step + ", side=" + side + ", wave=" + wave);
		
		//Se ho fatto tanti step quanti il numero dell'onda devo girare
		if(step == wave) {
			step = -1;
			side++;
			support.forward(tl);
			
			return;
		}
		
		//Se ho fatto tutti e quattro i lati dell'onda devo farne una nuova
		if(side == 5) {
			wave++;
			history.clear();
			side = 1;
		}
		
		//Vado avanti
		sendForward();
	}
	
	private void onSonarInformation(RobotInformation<SonarData> info) {
		System.out.println( this.myname + " | sonarInformation=" + info);
	}
	
	private void onSensorInformation(RobotInformation<SensorData> info) {
		System.out.println( this.myname + " | sensorInformation=" + info);
	}
	
	private void onMovement(RobotMovement move) {
		System.out.println( this.myname + " | movement=" + move);
		if(!blockMap) {
			moves.updateMovesRep(mapMoves.get(move.getMove()));
			moves.showRobotMovesRepresentation();
		}
		
		if(move.getEndMove().equals(MoveResult.SUCCEDED) && !obstacleFound) {
			
			long time = getTimeUnitOf(move.getMove());
			history.add(move, time);
			timeError = time - timeUnit;
			
			doWaveStep();
			
		} else if(move.getEndMove().equals(MoveResult.SUCCEDED) && obstacleFound) {
			doReturnToDenStep();
		}
		else {
			
			//Ignoro la collisione
			if(obstacleFound) {
				doReturnToDenStep();
				return;
			}
			
			obstacleFound = true;
			moves.setObstacle();
			moves.showRobotMovesRepresentation();
			//history.add(move, stopwatch.elapsed().toMillis() - timeError);
			history.add(move, getTimeUnitOf(move.getMove()));
			reverter.revert(history.getAsStream(), turnTimeUnit);
			
			counter = 3;
			blockMap = true;
			support.forward(speaker.encode(reverter.nextCommand()));
		}
		
	}
	
	private void doReturnToDenStep() {
		if(reverter.journeyEnded()) {
			obstacleFound = false;
			history.clear();
			
			//Passo all'onda successiva
			wave++;
			step = 0;
			side = 1;
			
			//Invio il comando di start dell'onda
			support.forward(fw);
		} else {
			RobotCommand next = reverter.nextCommand();
			
			if(counter > 0) {
				counter--;
				if(counter == 0)
					blockMap = false;
			}
			
			System.out.println( this.myname + " | return trip step = " + next);
			support.forward(speaker.encode(next));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleInput(String msg) {
		//System.out.println( this.myname + " | input=" + msg);
		
		if(stopwatch.isRunning())
			stopwatch.stop();
		
		if(msg.equals("startApp"))
			startExploring();
		else {
			RobotMovement move = speaker.parseMovement(msg);
			if(move != null) {
				onMovement(move);
				return;
			}
			
			RobotInformation<?> info = speaker.parseInformation(msg);
			if(info != null) {
				if(info.getData().getClass() == SensorData.class)
					onSensorInformation((RobotInformation<SensorData>) info);
				if(info.getData().getClass() == SonarData.class)
					onSonarInformation((RobotInformation<SonarData>) info);
				
				return;
			}
		}
	}
	
	public int getTimeUnitOf(Move move) {
		switch(move) {
		case MOVE_BACKWARD:
		case MOVE_FORWARD:
			return timeUnit;
		case TURN_RIGHT:
		case TURN_LEFT:
			return turnTimeUnit;
			
			default:
				return -1;
		}
	}

}
