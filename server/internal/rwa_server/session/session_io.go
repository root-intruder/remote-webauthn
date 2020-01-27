package session

import (
	"errors"
	"server/internal/rwa_server/message"
)

// Reads and executes incoming RP-client requests
func (sess *session) ReadClientRequest() (*message.Request, error) {
	var request message.Request
	err := sess.clientConn.ReadJSON(&request)
	if err != nil {
		return nil, err
	}

	return &request, nil
}

// Reads and executes incoming Auth-client requests
func (sess *session) ReadAuthenticatorRequest() (*message.Request, error) {
	if sess.authenticatorConn == nil {
		return nil, errors.New("No authenticator set ")
	}

	var request message.Request
	err := sess.authenticatorConn.ReadJSON(&request)
	if err != nil {
		return nil, err
	}

	return &request, nil
}

// Sends event to RP-client
func (sess *session) sendClientEvent(event message.Event) error {
	return sess.clientConn.WriteJSON(event)
}

// Sends event to Auth-client
func (sess *session) sendAuthenticatorEvent(event message.Event) error {
	if sess.authenticatorConn == nil {
		return errors.New("No authenticator set ")
	}

	return sess.authenticatorConn.WriteJSON(event)
}
