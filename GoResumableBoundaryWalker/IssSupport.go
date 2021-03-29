package main

import (
	"fmt"
	"net/url"
	"strings"

	"github.com/gorilla/websocket"
)

const MAX_BUFF = 20
const WS_URL = "ws://localhost:8091"

func StartSupport(commands_c chan string, movement_c chan string, sensor_c chan string, sonar_c chan string, terminate chan bool, done_out_supp chan bool, error_out_supp chan string) {

	fmt.Println("support\t| routine started")
	movement_in_supp := make(chan string, MAX_BUFF)
	sensor_in_supp := make(chan string, MAX_BUFF)
	sonar_in_supp := make(chan string, MAX_BUFF)

	u, _ := url.Parse(WS_URL)
	c, _, err := websocket.DefaultDialer.Dial(u.String(), nil)
	if err != nil {
		fmt.Println("support\t| cannot open websocket.\n\tError: " + err.Error())
		done_out_supp <- false
		error_out_supp <- err.Error()
		fmt.Println("support\t| terminated with errors")
		return
	}

	go readFromWs(c, movement_in_supp, sensor_in_supp, sonar_in_supp)
	done_out_supp <- true

	for {
		select {
		case x := <-whenS(len(terminate) == 0, commands_c):
			fmt.Println("support\t| received command")
			err := c.WriteMessage(websocket.TextMessage, []byte(x))
			if err != nil {
				fmt.Println("support\t| error writing on websocket: " + err.Error())
			}
			fmt.Println("support\t| written command on websocket: '" + x + "'")

		case x := <-whenS(len(terminate) == 0, movement_in_supp):
			fmt.Println("support\t| received movement")
			movement_c <- x

		case /*x := */ <-whenS(len(terminate) == 0, sensor_in_supp):
			fmt.Println("support\t| received sensor information")
			//sensor_c <- x

		case /*x := */ <-whenS(len(terminate) == 0, sonar_in_supp):
			fmt.Println("support\t| received sonar information")
			//sonar_c <- x

		case <-terminate:
			fmt.Println("support\t| received terminate")
			c.Close()
			done_out_supp <- true
			return
		}
	}
}

func readFromWs(conn *websocket.Conn, movement_out chan string, sensor_out chan string, sonar_out chan string) {
	fmt.Println("ws_reader\t| starting")
	for {
		_, msg, err := conn.ReadMessage()
		if err != nil {
			if !websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
				fmt.Println("ws_reader\t|connection closed")
				return
			}

			fmt.Printf("ws_reader\t| error: " + err.Error())
		}
		message := string(msg)
		fmt.Println("ws_reader\t| received message on websocket '" + message + "'")

		if strings.Contains(message, "endmove") {
			movement_out <- message
		} else if strings.Contains(message, "sonarName") {
			sonar_out <- message
		} else if strings.Contains(message, "collision") {
			sensor_out <- message
		}

	}
	fmt.Println("ws_reader\t| exit")

}
