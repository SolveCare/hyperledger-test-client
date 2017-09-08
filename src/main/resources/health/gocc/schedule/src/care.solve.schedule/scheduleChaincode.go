package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"strconv"

	"crypto/x509"
	"encoding/pem"
	"github.com/golang/protobuf/proto"
	mspprotos "github.com/hyperledger/fabric/protos/msp"

	"encoding/json"
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

	jsonResp := "{" +
		"\"ScheduleId\":\"" + schedule.ScheduleId + "\"," +
		"\"DoctorId\":\"" + schedule.DoctorId + "\"," +
		"\"Records\":" + strconv.Itoa(len(schedule.Records)) + "\"," +
		"}"

	return shim.Success([]byte(jsonResp))
}

func (s *ScheduleChaincode) registerToDoctor(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	patientId := args[0]
	doctorId := args[1]

	timeStart, err := strconv.ParseUint(args[2], 10, 64)
	if err != nil {
		return shim.Error(err.Error())
	}
	timeFinish, err := strconv.ParseUint(args[3], 10, 64)
	if err != nil {
		return shim.Error(err.Error())
	}

	description := args[4]

	_, err = s.getDoctor(stub, doctorId)
	if err != nil {
		return shim.Error(err.Error())
	}

	_, err = s.getPatient(stub, patientId)
	if err != nil {
		return shim.Error(err.Error())
	}

	slot := Slot{timeStart, timeFinish}

	scheduleRecordKey := "scheduleRecord:" + doctorId
	scheduleRecord := ScheduleRecord{scheduleRecordKey, description, patientId, slot}

	s.scheduler.Apply(stub, doctorId, scheduleRecord)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

func (s *ScheduleChaincode) createPatient(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	userId := args[0]
	email := args[1]
	firstName := args[2]
	lastName := args[3]

	balance, err := strconv.ParseFloat(args[4], 64)
	if err != nil {
		return shim.Error(fmt.Sprintf("Error while parsing petient balance: '%v'", err.Error()))
	}

	patient := Patient{userId, email, firstName, lastName, balance}

	err = s.savePatient(stub, patient)
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
	userId := args[0]
	email := args[1]
	firstName := args[2]
	lastName := args[3]
	level := args[4]

	doctor := Doctor{userId, email, firstName, lastName, level}

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
