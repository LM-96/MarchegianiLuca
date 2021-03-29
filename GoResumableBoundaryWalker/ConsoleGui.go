package main

import (
	"fmt"

	"github.com/tadvi/winc"
)

const MAX_BUFFER = 10

var stop_out_gui = make(chan bool, MAX_BUFFER)
var resume_out_gui = make(chan bool, MAX_BUFFER)
var terminate_out_gui = make(chan bool, MAX_BUFFER)
var done_in_gui = make(chan bool, MAX_BUFFER)
var errors_in_gui = make(chan string)

var all_ok = true

func main() {

	mainWindow := winc.NewForm(nil)
	mainWindow.SetSize(330, 150) // (width, height)
	mainWindow.SetText("ResumableBoundaryWalker")
	appIco, _ := winc.NewIconFromFile("./app.ico")
	mainWindow.SetIcon(0, appIco)

	resume := winc.NewPushButton(mainWindow)
	resume.SetText("RESUME")
	resume.SetPos(30, 10)   // (x, y)
	resume.SetSize(250, 40) // (width, height)
	resume.OnClick().Bind(func(e *winc.Event) {
		resume_out_gui <- true
	})

	stop := winc.NewPushButton(mainWindow)
	stop.SetText("STOP")
	stop.SetPos(30, 60)   // (x, y)
	stop.SetSize(250, 40) // (width, height)
	stop.OnClick().Bind(func(e *winc.Event) {
		stop_out_gui <- true
	})

	mainWindow.Center()
	mainWindow.Show()
	mainWindow.OnClose().Bind(wndOnClose)

	go StartResumableBoundaryWalkerActor(stop_out_gui, resume_out_gui, terminate_out_gui, done_in_gui, errors_in_gui)
	all_ok := <-done_in_gui

	if all_ok == false {
		errors := <-errors_in_gui
		fmt.Println("ConsoleGui\t| received error: " + errors)
		winc.Errorf(mainWindow, "Fatal Error:\n%s", errors)
		winc.Exit()
	}

	winc.RunMainLoop() // Must call to start event loop.
}

func wndOnClose(arg *winc.Event) {
	if all_ok {
		terminate_out_gui <- true
		<-done_in_gui
	}
	winc.Exit()
}
