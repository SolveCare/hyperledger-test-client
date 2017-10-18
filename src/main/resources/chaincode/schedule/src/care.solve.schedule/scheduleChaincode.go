package main

import (
	"fmt"
	"crypto/x509"
	"encoding/pem"
	"log"

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

	getSigner(stub)

	if function == "createPatient" {
		return s.patientService.createPatient(stub, args)
	}

	if function == "createDoctor" {
		return s.doctorService.createDoctor(stub, args)
	}

	if function == "getDoctor" {
		doctorId := args[0]
		doctor, err := s.doctorService.getDoctor(stub, doctorId)
		if err != nil {
			return shim.Error(err.Error())
		}

		doctorBytes, err := proto.Marshal(doctor)
		if err != nil {
			log.Fatalln("Failed to encode doctor:", err)
			return shim.Error(err.Error())
		}

		return shim.Success(doctorBytes)
	}

	if function == "getPatient" {
		patientId := args[0]
		patient, err := s.patientService.getPatient(stub, patientId)
		if err != nil {
			return shim.Error(err.Error())
		}

		patientBytes, err := proto.Marshal(patient)
		if err != nil {
			log.Fatalln("Failed to encode patient:", err)
			return shim.Error(err.Error())
		}

		return shim.Success(patientBytes)
	}

	if function == "getDoctorsSchedule" {
		return s.scheduleService.getDoctorsSchedule(stub, args)
	}

	if function == "registerToDoctor" {
		logger.Infof("scheduleService = %v", s.scheduleService)
		return s.scheduleService.registerToDoctor(stub, args)
	}

	return shim.Error(fmt.Sprintf("Unknown function '%v'", function))
}



func getSigner(stub shim.ChaincodeStubInterface) {
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
