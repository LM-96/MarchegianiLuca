package main

import (
	"fmt"
)

var halted = true
var step = 0
var last_endmove = MOVE_SUCCEEDED
var movements = ""

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

func StartResumableBoundaryWalkerActor(stop_in chan bool, resume_in chan bool, terminate_in chan bool, done_out chan bool, errors_out chan string) {

	fmt.Printf("boundary\t| making channels... ")
	command_out_walk := make(chan string, MAX_BUFFER)
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

	fw_cmd := NewEncodedRobotCommand(MOVE_FORWARD, 330)
	tl_cmd := NewEncodedRobotCommand(TURN_LEFT, 200)

	for {
		select {
		case <-whenB(len(terminate_in) == 0, resume_in):
			fmt.Println("boundary\t| received resume")
			halted = false
			if step >= 4 {
				step = 0
			}

		case <-whenB(len(terminate_in) == 0, stop_in):
			fmt.Println("boundary\t| received stop")
			halted = true

		case x := <-movement_in_walk:
			movement := DecodeMovement(x)
			fmt.Println("boundary\t| received movement")

			switch movement.move {
			case MOVE_FORWARD:
				movements = movements + "w"
			case TURN_LEFT:
				movements = movements + "l"
			default:
				movements = movements + "u"
			}

			if movement.endMove != last_endmove {
				last_endmove = movement.endMove
			}

		case <-terminate_in:
			fmt.Println("boundary\t| received terminate")
			terminate_out_walk <- true
			<-done_in_walk
			done_out <- true

			return
		}

		if !halted && step < 4 {
			if last_endmove == MOVE_SUCCEEDED {
				command_out_walk <- fw_cmd
			} else {
				command_out_walk <- tl_cmd
				step++
				if step >= 4 {
					halted = true
					fmt.Println("\n++ MovesInfo: " + movements + "\n")
				}
			}
		}

	}

	fmt.Println("boundary\t| sending stop message to the support")

	fmt.Println("boundary\t| support stopped")
	fmt.Println("boundary\t| terminated")

}
