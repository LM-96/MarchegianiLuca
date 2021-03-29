package main

import (
	"strconv"
	"strings"
)

/* ROBOT COMMAND **************************************************** */
const MOVE_FORWARD = "moveForward"
const MOVE_BACKWARD = "moveBackward"
const TURN_LEFT = "turnLeft"
const TURN_RIGHT = "turnRight"
const ALARM = "alarm"

type RobotCommand struct {
	robotmove string
	time      int
}

func (r *RobotCommand) GetRobotMove() string {
	return r.robotmove
}

func (r *RobotCommand) GetTime() int {
	return r.time
}

func NewRobotCommand(move string, time int) RobotCommand {
	res := RobotCommand{move, time}
	return res
}

func NewEncodedRobotCommand(move string, time int) string {
	res := ("{\"robotmove\":\"" + move + "\", \"time\":" + strconv.Itoa(time) + "}")
	return res
}

/* ****************************************************************** */

/* ROBOT MOVE ******************************************************* */
const MOVE_SUCCEEDED = "true"
const MOVE_FAILED = "false"
const MOVE_HALTED = "halted"
const MOVE_NOT_ALLOWED = "notallowed"

type RobotMovement struct {
	endMove string
	move    string
}

func (r *RobotMovement) GetEndMove() string {
	return r.endMove
}

func (r *RobotMovement) GetMove() string {
	return r.move
}

/* ****************************************************************** */

/* SONAR DATA ******************************************************* */
type SonarData struct {
	sonarName string
	distance  int
	axis      string
}

func (s *SonarData) GetSonarName() string {
	return s.sonarName
}

func (s *SonarData) GetDistance() int {
	return s.distance
}

func (s *SonarData) GetAxis() string {
	return s.axis
}

func NewSonarData(sonarName string, distance int, axis string) SonarData {
	res := SonarData{sonarName, distance, axis}
	return res
}

/* ****************************************************************** */

/* SENSOR DATA ****************************************************** */
type SensorData struct {
	collision bool
	move      string
}

func (s *SensorData) GetCollision() bool {
	return s.collision
}

func (s *SensorData) GetMove() string {
	return s.move
}

func NewSensorData(collision bool, move string) SensorData {
	res := SensorData{collision, move}
	return res
}

/* ****************************************************************** */

func Encode(command *RobotCommand) string {
	res := ("{\"robotmove\":\"" + command.robotmove + "\", \"time\":" + strconv.Itoa(command.time) + "}")
	return res
}

func DecodeMovement(jsonString string) RobotMovement {
	res := RobotMovement{}
	toks := strings.Split(jsonString, "\"")
	if toks[1] == "endmove" {
		res.endMove = toks[3]
		res.move = toks[7]
	}

	return res
}

func DecodeSensorData(jsonString string) SensorData {
	res := SensorData{}
	toks := strings.Split(jsonString, "\"")
	if toks[1] == "collision" {
		res.collision, _ = strconv.ParseBool(toks[3])
		res.move = toks[7]
	}

	return res
}

func DecodeSonarData(jsonString string) SonarData {
	res := SonarData{}
	toks := strings.Split(jsonString, "\"")
	if toks[1] == "sonarName" {
		res.sonarName = toks[3]

		dist := strings.Split(toks[7], ",")
		dist = strings.Split(dist[0], ":")
		res.distance, _ = strconv.Atoi(strings.Trim(dist[0], " "))

		res.axis = toks[10]
	}

	return res
}
