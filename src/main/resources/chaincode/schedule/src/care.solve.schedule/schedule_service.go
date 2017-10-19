package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/golang/protobuf/proto"
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

func (s *ScheduleService) getDoctorsSchedule(stub shim.ChaincodeStubInterface, doctorId string) (*Schedule, error) {

	_, err := s.doctorService.getDoctorById(stub, doctorId)
	if err != nil {
		return nil, err
	}

	schedule, err := s.scheduler.Get(stub, doctorId)
	if err != nil {
		return nil, err
	}

	return schedule, err
}

func (s *ScheduleService) createScheduleRecord(stub shim.ChaincodeStubInterface, scheduleRequest ScheduleRequest) (*ScheduleRecord, error) {

	_, err := s.doctorService.getDoctorById(stub, scheduleRequest.DoctorId)
	if err != nil {
		return nil, err
	}

	_, err = s.patientService.getPatientById(stub, scheduleRequest.PatientId)
	if err != nil {
		return nil, err
	}

	scheduleRecordKey := "scheduleRecord:" + scheduleRequest.DoctorId
	scheduleRecord := ScheduleRecord {
		scheduleRecordKey,
		scheduleRequest.Description,
		scheduleRequest.PatientId,
		scheduleRequest.Slot,
	}

	err = s.scheduler.Apply(stub, scheduleRequest.DoctorId, scheduleRecord)
	if err != nil {
		return nil, err
	}

	return &scheduleRecord, nil
}

func (s *ScheduleService) decodeProtoByteString(scheduleRequestByteString string) (*ScheduleRequest, error) {
	var err error

	scheduleRequest := ScheduleRequest{}
	err = proto.UnmarshalText(scheduleRequestByteString, &scheduleRequest)
	if err != nil {
		logger.Errorf("Error while unmarshalling ScheduleRequest: %v", err.Error())
	}

	return &scheduleRequest, err
}