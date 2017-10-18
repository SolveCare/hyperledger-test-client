package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"fmt"
	"github.com/golang/protobuf/proto"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type PatientService struct {

}

func (s *PatientService) createPatient(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err error

	encodedPatientByteString := args[0]

	patient := Patient{}
	err = proto.Unmarshal([]byte(encodedPatientByteString), &patient)
	if err != nil {
		logger.Errorf("Error while unmarshalling Patient: %v", err.Error())
		//return shim.Error(err.Error())
	}

	err = s.savePatient(stub, patient)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)

}

func (t *PatientService) savePatient(stub shim.ChaincodeStubInterface, patient Patient) error {
	fmt.Printf("> Saving patient %v \n", patient)

	jsonUser, err := proto.Marshal(&patient)

	patientKey := "patient:" + patient.UserId

	err = stub.PutState(patientKey, jsonUser)
	if err != nil {
		return err
	}

	return nil
}

func (t *PatientService) getPatient(stub shim.ChaincodeStubInterface, patientId string) (*Patient, error) {

	patientKey := "patient:" + patientId
	patientBytes, err := stub.GetState(patientKey)
	if err != nil {
		return nil, err
	}

	fmt.Printf("Getting patient %v \n", string(patientBytes))

	var patient Patient
	proto.Unmarshal(patientBytes, &patient)
	return &patient, nil
}