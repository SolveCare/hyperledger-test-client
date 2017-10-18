package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/golang/protobuf/proto"
	"log"
	pb "github.com/hyperledger/fabric/protos/peer"
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

func (s *ScheduleService) getDoctorsSchedule(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	doctorId := args[0]

	_, err := s.doctorService.getDoctor(stub, doctorId)
	if err != nil {
		return shim.Error(err.Error())
	}

	schedule, err := s.scheduler.Get(stub, doctorId)
	if err != nil {
		return shim.Error(err.Error())
	}

	scheduleBytes, err := proto.Marshal(schedule)
	if err != nil {
		log.Fatalln("Failed to encode schedule:", err)
		return shim.Error(err.Error())
	}

	return shim.Success(scheduleBytes)
}

func (s *ScheduleService) registerToDoctor(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	scheduleRequestByteString := args[0];

	logger.Infof("> scheduleRequestByteString: %v \n", scheduleRequestByteString)

	scheduleRequest := ScheduleRequest{}
	proto.UnmarshalText(scheduleRequestByteString, &scheduleRequest)

	_, err := s.doctorService.getDoctor(stub, scheduleRequest.DoctorId)
	if err != nil {
		return shim.Error(err.Error())
	}

	_, err = s.patientService.getPatient(stub, scheduleRequest.PatientId)
	if err != nil {
		return shim.Error(err.Error())
	}

	scheduleRecordKey := "scheduleRecord:" + scheduleRequest.DoctorId
	scheduleRecord := ScheduleRecord {
		scheduleRecordKey,
		scheduleRequest.Description,
		scheduleRequest.PatientId,
		scheduleRequest.Slot,
	}

	s.scheduler.Apply(stub, scheduleRequest.DoctorId, scheduleRecord)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}