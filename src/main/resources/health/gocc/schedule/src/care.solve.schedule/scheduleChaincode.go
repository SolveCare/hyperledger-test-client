package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"

	"crypto/x509"
	"encoding/pem"
	"github.com/golang/protobuf/proto"
	mspprotos "github.com/hyperledger/fabric/protos/msp"

	"encoding/json"
	"log"
)

type ScheduleChaincode struct {
	scheduler Scheduler
}

func (s *ScheduleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	s.scheduler = SchedulerImpl{}

	return shim.Success(nil)
}

func (s *ScheduleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()

	fmt.Printf("=====================================================\n")
	fmt.Printf("Invoking function %v with args %v  \n", function, args)
	getSigner(stub)

	if function == "createPatient" {
		return s.createPatient(stub, args)
	}

	if function == "createDoctor" {
		return s.createDoctor(stub, args)
	}

	if function == "getDoctor" {
		doctorId := args[0]
		doctor, err := s.getDoctor(stub, doctorId)
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
		patient, err := s.getPatient(stub, patientId)
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
		return s.getDoctorsSchedule(stub, args)
	}

	if function == "registerToDoctor" {
		return s.registerToDoctor(stub, args)
	}

	return shim.Error(fmt.Sprintf("Unknown function '%v'", function))
}

func (s *ScheduleChaincode) getDoctorsSchedule(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	doctorId := args[0]

	_, err := s.getDoctor(stub, doctorId)
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

func (s *ScheduleChaincode) registerToDoctor(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	scheduleRequestByteString := args[0];

	fmt.Printf("> scheduleRequestByteString: %v \n", scheduleRequestByteString)

	scheduleRequest := ScheduleRequest{}
	proto.UnmarshalText(scheduleRequestByteString, &scheduleRequest)

	// fmt.Printf("> scheduleRequestByteString: %v \n", *(scheduleRequest.Slot))

	_, err := s.getDoctor(stub, scheduleRequest.DoctorId)
	if err != nil {
		return shim.Error(err.Error())
	}

	_, err = s.getPatient(stub, scheduleRequest.PatientId)
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

func (s *ScheduleChaincode) createPatient(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	encodedPatientByteString := args[0];

	patient := Patient{}
	proto.Unmarshal([]byte(encodedPatientByteString), &patient)

	err := s.savePatient(stub, patient)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)

}

func (t *ScheduleChaincode) savePatient(stub shim.ChaincodeStubInterface, patient Patient) error {
	fmt.Printf("Saving patient %v \n", patient)

	jsonUser, err := json.Marshal(patient)

	patientKey := "patient:" + patient.UserId

	err = stub.PutState(patientKey, jsonUser)
	if err != nil {
		return err
	}

	return nil
}

func (s *ScheduleChaincode) createDoctor(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	encodedDoctorByteString := args[0];

	doctor := Doctor{}
	proto.Unmarshal([]byte(encodedDoctorByteString), &doctor)

	err := s.saveDoctor(stub, doctor)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

func (t *ScheduleChaincode) saveDoctor(stub shim.ChaincodeStubInterface, doctor Doctor) error {
	fmt.Printf("Saving doctor %v \n", doctor)

	jsonUser, err := json.Marshal(doctor)

	doctorKey := "doctor:" + doctor.UserId
	err = stub.PutState(doctorKey, jsonUser)
	if err != nil {
		return err
	}

	return nil
}

func (t *ScheduleChaincode) getDoctor(stub shim.ChaincodeStubInterface, doctorId string) (*Doctor, error) {

	doctorKey := "doctor:" + doctorId
	doctorBytes, err := stub.GetState(doctorKey)
	if err != nil {
		return nil, err
	}

	fmt.Printf("Getting doctor %v \n", string(doctorBytes))

	var doctor Doctor
	json.Unmarshal(doctorBytes, &doctor)
	return &doctor, nil
}

func (t *ScheduleChaincode) getPatient(stub shim.ChaincodeStubInterface, patientId string) (*Patient, error) {

	patientKey := "patient:" + patientId
	patientBytes, err := stub.GetState(patientKey)
	if err != nil {
		return nil, err
	}

	fmt.Printf("Getting patient %v \n", string(patientBytes))

	var patient Patient
	json.Unmarshal(patientBytes, &patient)
	return &patient, nil
}

func getSigner(stub shim.ChaincodeStubInterface) {
	fmt.Printf("*********************************\n")
	creator,err := stub.GetCreator()
	if err != nil {
		fmt.Printf("> Error: %v \n", err.Error())
	}

	id := &mspprotos.SerializedIdentity{}
	err = proto.Unmarshal(creator, id)

	fmt.Printf("> Creator: %v \n", string(creator))

	block, _ := pem.Decode(id.GetIdBytes())
	cert,err := x509.ParseCertificate(block.Bytes)
	enrollID := cert.Subject.CommonName
	fmt.Printf("> enrollID: %v \n", string(enrollID))
	mspID := id.GetMspid()
	fmt.Printf("> mspID: %v \n", string(mspID))
	fmt.Printf("*********************************\n")
}

func main() {
	err := shim.Start(new(ScheduleChaincode))
	if err != nil {
		fmt.Printf("Error starting ScheduleChaincode: %s", err)
	}
}
