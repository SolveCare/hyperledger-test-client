package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"fmt"
	"encoding/json"
	"errors"
)

type SchedulerImpl struct {
}

func (s SchedulerImpl) ConstructScheduleKey(doctorId string) string {
	return "schedule:doctorId:" + doctorId
}

func (s SchedulerImpl) Get(stub shim.ChaincodeStubInterface, doctorId string) (*Schedule, error) {
	scheduleId := s.ConstructScheduleKey(doctorId)
	scheduleBytes, err := stub.GetState(scheduleId)

	if err != nil {
		return nil, err
	}

	var schedule Schedule
	if scheduleBytes == nil {
		return nil, errors.New(fmt.Sprintf("Schedule with key '%v' not found", scheduleId))
	} else {
		json.Unmarshal(scheduleBytes, &schedule)
		logger.Infof("Retrieve schedule: %v", schedule)
	}

	return &schedule, nil
}

func (s SchedulerImpl) Apply(stub shim.ChaincodeStubInterface, schedule Schedule) (*Schedule, error) {
	scheduleKey := s.ConstructScheduleKey(schedule.DoctorId)

	scheduleBytes, err := stub.GetState(scheduleKey)

	if scheduleBytes != nil {
		errorMsg := fmt.Sprintf("Schedule with key '%v' already exists", scheduleKey)
		logger.Errorf(errorMsg)
		errors.New(errorMsg)
	}

	logger.Infof("Creating new schedule for doctor %v", schedule.DoctorId)

	schedule.ScheduleId = scheduleKey;

	jsonSchedule, err := json.Marshal(schedule)
	if err != nil {
		return nil, err
	}

	err = stub.PutState(scheduleKey, jsonSchedule)
	if err != nil {
		return nil, err
	}

	return &schedule, nil
}