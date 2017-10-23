package main

import "github.com/hyperledger/fabric/core/chaincode/shim"

type Scheduler interface {

	Get(stub shim.ChaincodeStubInterface, scheduleId string) (*Schedule, error)
	Apply(stub shim.ChaincodeStubInterface, schedule Schedule) (*Schedule, error)
	ConstructScheduleKey(doctorId string) string
}
