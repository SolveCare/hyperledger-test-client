package main

import (
	"fmt"
	"crypto/x509"
	"encoding/pem"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/golang/protobuf/proto"

	mspprotos "github.com/hyperledger/fabric/protos/msp"
	pb "github.com/hyperledger/fabric/protos/peer"
)

var logger = shim.NewLogger("schedule_chaincode")

type ScheduleChaincode struct {
	scheduler SchedulerImpl
	doctorService *DoctorService
	patientService *PatientService

	scheduleService *ScheduleService
}

func (s *ScheduleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	s.scheduler = SchedulerImpl{}
	logger.Infof("Created Scheduler: %v", s.scheduler)

	s.doctorService = &DoctorService{}
	logger.Infof("Created DoctorService: %v", s.doctorService)

	s.patientService = &PatientService{}
	logger.Infof("Created PatientService: %v", s.patientService)

	s.scheduleService = new(ScheduleService).New(&s.scheduler, s.doctorService, s.patientService)
	logger.Infof("Created ScheduleService: %v", s.scheduleService)

	return shim.Success(nil)
}

func (s *ScheduleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()

	logger.Infof("=====================================================")
	logger.Infof("Invoking function %v with args %v", function, args)

	printSigner(stub)

	if function == "createPatient" {
		encodedPatientByteString := args[0]
		patient, err := s.patientService.decodeProtoByteString(encodedPatientByteString)
		if err != nil {
			//return shim.Error(err.Error()) //todo: investigate 'proto: bad wiretype for field main.Patient.UserId: got wiretype 1, want 2'
		}
		savedPatient, err := s.patientService.savePatient(stub, *patient)
		if err != nil {
			return shim.Error(err.Error())
		}
		return s.getResponseWithProto(savedPatient)
	}

	if function == "createDoctor" {
		encodedDoctorByteString := args[0]
		doctor, err := s.doctorService.decodeProtoByteString(encodedDoctorByteString)
		if err != nil {
			return shim.Error(err.Error())
		}
		savedDoctor, err := s.doctorService.saveDoctor(stub, *doctor)
		if err != nil {
			return shim.Error(err.Error())
		}
		return s.getResponseWithProto(savedDoctor)
	}

	if function == "getDoctor" {
		doctorId := args[0]
		doctor, err := s.doctorService.getDoctorById(stub, doctorId)
		if err != nil {
			return shim.Error(err.Error())
		}
		return s.getResponseWithProto(doctor)
	}

	if function == "getPatient" {
		patientId := args[0]
		patient, err := s.patientService.getPatientById(stub, patientId)
		if err != nil {
			return shim.Error(err.Error())
		}
		return s.getResponseWithProto(patient)
	}

	if function == "getDoctorsSchedule" {
		doctorId := args[0]
		schedule, err := s.scheduleService.getDoctorsSchedule(stub, doctorId)
		if err != nil {
			return shim.Error(err.Error())
		}
		return s.getResponseWithProto(schedule)
	}

	if function == "registerToDoctor" {
		scheduleRequestByteString := args[0];
		scheduleRequest, err := s.scheduleService.decodeProtoByteString(scheduleRequestByteString)
		if err != nil {
			return shim.Error(err.Error())
		}
		scheduleRecord, err := s.scheduleService.createScheduleRecord(stub, *scheduleRequest)
		return s.getResponseWithProto(scheduleRecord)
	}

	return shim.Error(fmt.Sprintf("Unknown function '%v'", function))
}

func (s *ScheduleChaincode) getResponseWithProto(message proto.Message) pb.Response {
	doctorBytes, err := proto.Marshal(message)
	if err != nil {
		logger.Errorf("Failed to marshall message: '%v'. Error: %v", message, err)
		return shim.Error(err.Error())
	}

	return shim.Success(doctorBytes)
}

func printSigner(stub shim.ChaincodeStubInterface) {
	logger.Infof("*********************************")
	creator,err := stub.GetCreator()
	if err != nil {
		logger.Errorf("Error: %v", err.Error())
	}

	id := &mspprotos.SerializedIdentity{}
	err = proto.Unmarshal(creator, id)

	logger.Infof("Creator: %v", string(creator))

	block, _ := pem.Decode(id.GetIdBytes())
	cert,err := x509.ParseCertificate(block.Bytes)

	enrollID := cert.Subject.CommonName
	logger.Infof("enrollID: %v", string(enrollID))

	mspID := id.GetMspid()
	logger.Infof("mspID: %v \n", string(mspID))
	logger.Infof("*********************************")
}

func main() {
	err := shim.Start(new(ScheduleChaincode))
	if err != nil {
		logger.Errorf("Error starting ScheduleChaincode: %s", err)
	}
}
