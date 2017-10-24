package main

import "github.com/hyperledger/fabric/core/chaincode/shim"

type Scheduler interface {

	Get(stub shim.ChaincodeStubInterface, ownerId string) (*Schedule, error)
	Apply(stub shim.ChaincodeStubInterface, schedule Schedule) (*Schedule, error)
	ConstructScheduleKey(ownerId string) string
}
