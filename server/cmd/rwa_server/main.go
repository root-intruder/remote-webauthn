package main

import (
	"github.com/gorilla/mux"
	"log"
	"net/http"
	"server/internal/rwa_server/handler"
)

// Starts up server and offers wss endpoints for RP- and Auth-client
func main() {
	router := mux.NewRouter()
	router.HandleFunc("/session", handler.HandleClientWebsocket)
	router.HandleFunc("/session/{sessionId}", handler.HandleAuthenticatorWebsocket)

	log.Fatal(http.ListenAndServe(":8081", router))
}
