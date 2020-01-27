package handler

import (
	"net/http"
	"server/internal/rwa_server/message"
	"server/internal/rwa_server/session"
)

// Handles incoming RP-client session initialization
func HandleClientWebsocket(w http.ResponseWriter, r *http.Request) {
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		return
	}

	sessId, sess, err := session.New(conn)
	if err != nil {
		_ = conn.Close()
		return
	}
	sessions[sessId] = sess

	go handleClientSession(sessId)
}

// Handles RP-client session until closed
func handleClientSession(sessId string) {
	sess := sessions[sessId]

	for {
		req, err := sess.ReadClientRequest()
		if err != nil {
			terminateSession(sessId)
			break
		}

		switch req.Op {
		case message.ForwardRequestRequest:
			err := sess.ForwardRequest(req.Payload)
			if err != nil {
				continue
			}
		}
	}
}
