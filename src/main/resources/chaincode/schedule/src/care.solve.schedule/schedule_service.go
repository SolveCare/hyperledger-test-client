package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/golang/protobuf/proto"
	"encoding/json"
	"github.com/google/uuid"
)

type ScheduleService struct {
	scheduler Scheduler
	doctorService *DoctorService
	patientService *PatientService
}

func (s *ScheduleService) New(scheduler *SchedulerImpl, doctorService *DoctorService, patientService *PatientService) *ScheduleService {
	s.scheduler = scheduler
	s.doctorService = doctorService
	s.patientService = patientService

	return s
}

func (s *ScheduleService) getScheduleByOwnerId(stub shim.ChaincodeStubInterface, ownerId string) (*Schedule, error) {
	schedule, err := s.scheduler.Get(stub, ownerId)
	if err != nil {
		return nil, err
	}

	return schedule, err
}

func (s *ScheduleService) createSchedule(stub shim.ChaincodeStubInterface, schedule Schedule) (*Schedule, error) {
	savedSchedule, err := s.scheduler.Apply(stub, schedule)
	if err != nil {
		return nil, err
	}

	return savedSchedule, nil
}

func (s *ScheduleService) createSlot(stub shim.ChaincodeStubInterface, scheduleId string, slot Slot) (*Slot, error) {
	var err error

	schedule, err := s.scheduler.Get(stub, scheduleId)
	if err != nil {
		return nil, err
	}

	slot.SlotId = uuid.New().String()
	logger.Infof("Add new slot: %v to schedule %v", slot, scheduleId)

	schedule.Slots = append(schedule.Slots, &slot)
	logger.Infof("schedule.Slots: %v", schedule.Slots)

	jsonSchedule, err := json.Marshal(schedule)
	logger.Infof("jsonSchedule: %v", string(jsonSchedule))
	if err != nil {
		logger.Errorf("Error while marshalling Schedule: %v", err.Error())
		return nil, err
	}

	err = stub.PutState(s.scheduler.ConstructScheduleKey(scheduleId), jsonSchedule)
	if err != nil {
		logger.Errorf("Error while updating Schedule: %v", err.Error())
		return nil, err
	}

	return &slot, nil
}

func (s *ScheduleService) updateSlot(stub shim.ChaincodeStubInterface, scheduleId string, slotId string, newSlot Slot) error {
	var err error

	schedule, err := s.scheduler.Get(stub, scheduleId)
	if err != nil {
		logger.Errorf("Error while retrieving Schedule: %v", err.Error())
		return err
	}

	for i, currentSlot := range schedule.Slots {
		if currentSlot.SlotId == slotId {
			existedSlot := schedule.Slots[i];
			if newSlot.TimeStart > 0 {
				existedSlot.TimeStart = newSlot.TimeStart
			}
			if newSlot.TimeFinish > 0 {
				existedSlot.TimeFinish = newSlot.TimeFinish
			}

			if newSlot.RegistrationInfo != nil && newSlot.RegistrationInfo.AttendeeId != "" {
				newSlot.Avaliable = Slot_BUSY
			} else {
				existedSlot.Avaliable = newSlot.Avaliable
			}
			existedSlot.RegistrationInfo = newSlot.RegistrationInfo
			break
		}
	}

	jsonSchedule, err := json.Marshal(schedule)
	if err != nil {
		logger.Errorf("Error while marshalling Schedule: %v", err.Error())
		return err
	}

	err = stub.PutState(s.scheduler.ConstructScheduleKey(scheduleId), jsonSchedule)
	if err != nil {
		logger.Errorf("Error while updating Schedule: %v", err.Error())
		return err
	}

	return nil
}

func (s *ScheduleService) decodeScheduleByteString(scheduleByteString string) (*Schedule, error) {
	var err error

	schedule := Schedule{}
	err = proto.Unmarshal([]byte(scheduleByteString), &schedule)
	if err != nil {
		logger.Errorf("Error while unmarshalling Schedule: %v", err.Error())
	}

	return &schedule, err
}

func (s *ScheduleService) decodeSlotByteString(scheduleByteString string) (*Slot, error) {
	var err error

	slot := Slot{}
	err = proto.UnmarshalText(scheduleByteString, &slot)
	if err != nil {
		logger.Errorf("Error while unmarshalling Slot: %v", err.Error())
	}

	return &slot, err
}