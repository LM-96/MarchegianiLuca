package main

import (
	"fmt"
	"strconv"
)

var halted = true
var step = 0
var last_endmove = MOVE_SUCCEEDED
var movements = ""
var state = "paused"
var savedState = ""
var savedMove = ""
var savedEndmove = ""
var CMD_OUT chan string
var FW_CMD = NewEncodedRobotCommand(MOVE_FORWARD, 330)
var TL_CMD = NewEncodedRobotCommand(TURN_LEFT, 200)

var rbw_name = "robotAppl"

/* WHEN (GUARDIA LOGICA) ******************************************** */
func whenB(b bool, c chan bool) chan bool {
	if !b {
		return nil
	}
	return c
}
func whenS(b bool, c chan string) chan string {
	if !b {
		return nil
	}
	return c
}

func fsm(x string, y string) {
	fmt.Println(rbw_name + " | fsm state=" + state + " stepNum=" + strconv.Itoa(step) + " move=" + x + " endmove=" + y)
	switch state {
	case "paused":
		if x == "RESUME" && y == "robotcmd" {
			step = 1
			doStep()
			state = "walking"
		}
		break

	case "walking":
		if x == "moveForward" && y == "true" {
			movements += "w"
			doStep()
		} else if x == "moveForward" && y == "false" {
			state = "obstacle"
			turnLeft()
		} else if x == "STOP" && y == "robotcmd" {
			savedState = state
			state = "halted"
		}

		showRobotMovesRepresentation()
		break

	case "obstacle":
		if x == "turnLeft" && y == "true" {
			movements += "l"
			showRobotMovesRepresentation()

			if step < 4 {
				step++
				state = "walking"
				doStep()
			} else {
				state = "paused"
				step = 0
				cleanrMovesRepresentation()

			}
		} else if x == "STOP" && y == "robotcmd" {
			savedState = state
			state = "halted"
		}
		break

	case "halted":
		if x == "RESUME" && y == "robotcmd" {
			state = savedState
			resume()

		} else if y == "true" {
			saveMsg(x, y)
		}
		break

	default:
		fmt.Println("error - curState = " + state)
	}
}

func StartResumableBoundaryWalkerActor(stop_in chan bool, resume_in chan bool, terminate_in chan bool, done_out chan bool, errors_out chan string) {

	fmt.Println("boundary\t| VERSION 2 (FSM)")

	fmt.Printf("boundary\t| making channels... ")
	command_out_walk := make(chan string, MAX_BUFFER)
	CMD_OUT = command_out_walk
	movement_in_walk := make(chan string, MAX_BUFFER)
	sonar_in_walk := make(chan string, MAX_BUFFER)
	sensor_in_walk := make(chan string, MAX_BUFFER)
	terminate_out_walk := make(chan bool, MAX_BUFFER)
	done_in_walk := make(chan bool)
	errors_in_walk := make(chan string)
	fmt.Println("channel maked")

	fmt.Println("boundary\t| starting support...")
	go StartSupport(command_out_walk, movement_in_walk, sensor_in_walk, sonar_in_walk, terminate_out_walk, done_in_walk, errors_in_walk)
	all_ok_in_supp := <-done_in_walk

	if !all_ok_in_supp {
		fmt.Println("boundary\t| support notifies an error: can't continue")
		fmt.Println("boundary\t| terminated with errors")
		done_out <- false
		errors := <-errors_in_walk
		errors_out <- errors
		return
	}
	fmt.Println("boundary\t| connection enstablished, support correctly started")
	done_out <- true

	for {
		select {
		case <-whenB(len(terminate_in) == 0, resume_in):
			fsm("RESUME", "robotcmd")

		case <-whenB(len(terminate_in) == 0, stop_in):
			fsm("STOP", "robotcmd")

		case x := <-whenS(len(terminate_in) == 0, movement_in_walk):
			movement := DecodeMovement(x)
			fsm(movement.move, movement.endMove)

		case <-terminate_in:
			fmt.Println("boundary\t| received terminate")
			terminate_out_walk <- true
			<-done_in_walk
			done_out <- true

			return
		}

	}

}

func doStep() {
	CMD_OUT <- FW_CMD
}

func turnLeft() {
	CMD_OUT <- TL_CMD
}

func showRobotMovesRepresentation() {
	fmt.Println("++ MovesRepresentation: " + movements)
}

func cleanrMovesRepresentation() {
	movements = ""
}

func resume() {

	if savedEndmove != "" && savedMove != "" {
		savedMoveRes := savedMove
		savedMove = ""
		savedEndmoveRes := savedEndmove
		savedEndmove = ""

		fsm(savedMoveRes, savedEndmoveRes)
	}
}

func saveMsg(x string, y string) {
	savedMove = x
	savedEndmove = y
}
