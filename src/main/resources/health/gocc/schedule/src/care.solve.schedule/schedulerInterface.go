package main

import "github.com/hyperledger/fabric/core/chaincode/shim"

type AppointmentScheduler interface {

	// Return doctor's schedule in specified date range
	// Schedule representing as collection of Slot
	GetSchedule(stub shim.ChaincodeStubInterface, doctorId string, dateRange DateRange) (*[]Slot, error)

	// Return earliest avaliable slot
	GetEarliestAvailableSlot(stub shim.ChaincodeStubInterface, doctorId string) (*Slot, error)

	// Create patient appointment on particular slot
	// Return appointmentId
	ApplyForVisit(stub shim.ChaincodeStubInterface, doctorId string, patientId string, timeSlotId string) (*string, error)

	// Cancel patient appointment
	CancelAppointment(stub shim.ChaincodeStubInterface, appId string) error

}