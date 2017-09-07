package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"fmt"
	"encoding/json"
	"errors"
)

type SchedulerImpl struct {
}

func (s SchedulerImpl) constructScheduleId(scheduleId string) string {
	return "schedule:" + scheduleId
}

func (s SchedulerImpl) Get(stub shim.ChaincodeStubInterface, scheduleId string) (*Schedule, error) {
	scheduleKey := s.constructScheduleId(scheduleId)
	scheduleBytes, err := stub.GetState(scheduleKey)

	if err != nil {
		return nil, err
	}

	var schedule Schedule
	if scheduleBytes == nil {
		return nil, errors.New(fmt.Sprintf("Schedule with id '%v' not found", scheduleId))
	} else {
		json.Unmarshal(scheduleBytes, &schedule)
		fmt.Printf("Retrieve schedule: %v \n", schedule)
	}

	return &schedule, nil
}

func (s SchedulerImpl) Apply(stub shim.ChaincodeStubInterface, scheduleId string, scheduleRecord ScheduleRecord) error {
	scheduleKey := s.constructScheduleId(scheduleId)
	scheduleBytes, err := stub.GetState(scheduleKey)

	var schedule Schedule
	if scheduleBytes == nil {
		fmt.Printf("Empty schedule for doctor %v. Creating new... \n", scheduleId)
		schedule = Schedule{scheduleKey, scheduleId, make(map[string]ScheduleRecord)}
	} else {
		fmt.Printf("Found schedule for doctor %v \n", scheduleId)
		json.Unmarshal(scheduleBytes, &schedule)
	}

	slot := scheduleRecord.Slot
	slotJson, err := json.Marshal(slot)
	if err != nil {
		return err
	}

	slotString := string(slotJson)
	schedule.Records[slotString] = scheduleRecord

	jsonSchedule, err := json.Marshal(schedule)
	if err != nil {
		return err
	}

	err = stub.PutState(scheduleKey, jsonSchedule)
	if err != nil {
		return err
	}

	return nil
}