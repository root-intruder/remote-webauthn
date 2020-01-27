package handler

import (
	"github.com/gorilla/websocket"
	"net/http"
	"server/internal/rwa_server/session"
)

// Shared session storage
var sessions = make(map[string]session.Session)

// Common HTTP -> WS upgrade logic
var upgrader = websocket.Upgrader{
	CheckOrigin: func(_ *http.Request) bool {
		return true
	},
}

// Terminates both connections and removes session from map
func terminateSession(sessId string) {
	// Only terminate if session still exists
	if sess, ok := sessions[sessId]; ok {
		sess.Terminate()
		delete(sessions, sessId)
	}
}
