package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"fmt"
	"encoding/json"
	"errors"
)

type SchedulerImpl struct {
}

func (s SchedulerImpl) ConstructScheduleKey(ownerId string) string {
	return "schedule:ownerId:" + ownerId
}

func (s SchedulerImpl) Get(stub shim.ChaincodeStubInterface, ownerId string) (*Schedule, error) {
	scheduleId := s.ConstructScheduleKey(ownerId)
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
	scheduleKey := s.ConstructScheduleKey(schedule.OwnerId)

	scheduleBytes, err := stub.GetState(scheduleKey)

	if scheduleBytes != nil {
		errorMsg := fmt.Sprintf("Schedule with key '%v' already exists", scheduleKey)
		logger.Errorf(errorMsg)
		errors.New(errorMsg)
	}

	logger.Infof("Creating new schedule for owner %v", schedule.OwnerId)

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

//func (s SchedulerImpl) saveDoctorSchedule(stub shim.ChaincodeStubInterface, schedule Schedule) (*Schedule, error) {
//	scheduleKey := s.ConstructDoctorScheduleKey(schedule.OwnerId)
//
//	logger.Infof("Creating new schedule for doctor %v", schedule.OwnerId)
//
//	schedule.ScheduleId = scheduleKey;
//
//	jsonSchedule, err := json.Marshal(schedule)
//	if err != nil {
//		return nil, err
//	}
//
//	err = stub.PutState(scheduleKey, jsonSchedule)
//	if err != nil {
//		return nil, err
//	}
//
//	return &schedule, nil
//}
//
//func (s SchedulerImpl) savePatientSchedule(stub shim.ChaincodeStubInterface, schedule Schedule) (*Schedule, error) {
//	scheduleKey := s.ConstructPatientScheduleKey(schedule.OwnerId)
//
//	logger.Infof("Creating new schedule for patient %v", schedule.OwnerId)
//
//	schedule.ScheduleId = scheduleKey;
//
//	jsonSchedule, err := json.Marshal(schedule)
//	if err != nil {
//		return nil, err
//	}
//
//	err = stub.PutState(scheduleKey, jsonSchedule)
//	if err != nil {
//		return nil, err
//	}
//
//	return &schedule, nil
//}