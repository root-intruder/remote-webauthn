package handler

import (
	"github.com/gorilla/mux"
	"log"
	"net/http"
	"server/internal/rwa_server/message"
)

// Handles incoming Auth-client session join
func HandleAuthenticatorWebsocket(w http.ResponseWriter, r *http.Request) {
	sessId := mux.Vars(r)["sessionId"]
	if sess, ok := sessions[sessId]; ok {
		conn, err := upgrader.Upgrade(w, r, nil)
		if err != nil {
			log.Fatal(err)
		}

		err = sess.SetAuthenticator(conn)
		if err != nil {
			terminateSession(sessId)
			return
		}

		go handleAuthenticatorSession(sessId)
	} else {
		w.WriteHeader(404)
	}
}

// Handles Auth-client session until closed
func handleAuthenticatorSession(sessId string) {
	sess := sessions[sessId]

	for {
		req, err := sess.ReadAuthenticatorRequest()
		if err != nil {
			terminateSession(sessId)
			break
		}

		switch req.Op {
		case message.ForwardResponseRequest:
			err := sess.ForwardResponse(req.Payload)
			if err == nil {
				terminateSession(sessId)
			}
		}
	}
}
