package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/golang/protobuf/proto"
)

type PatientService struct {

}

func (s *PatientService) decodeProtoByteString(encodedPatientByteString string) (*Patient, error) {
	var err error

	patient := Patient{}
	err = proto.Unmarshal([]byte(encodedPatientByteString), &patient)
	if err != nil {
		logger.Errorf("Error while unmarshalling Patient: %v", err.Error())
	}

	return &patient, err
}

func (t *PatientService) savePatient(stub shim.ChaincodeStubInterface, patient Patient) (*Patient, error) {
	logger.Infof("Saving patient %v", patient)

	jsonUser, err := proto.Marshal(&patient)

	patientKey := "patient:" + patient.UserId

	err = stub.PutState(patientKey, jsonUser)
	if err != nil {
		logger.Errorf("Error while saving patient '%v'. Error: %v", patient, err)
		return nil, err
	}

	return &patient, nil
}

func (t *PatientService) getPatientById(stub shim.ChaincodeStubInterface, patientId string) (*Patient, error) {

	patientKey := "patient:" + patientId
	patientBytes, err := stub.GetState(patientKey)
	if err != nil {
		logger.Errorf("Error while getting patient with key '%v'. Error: %v", patientKey, err)
		return nil, err
	}

	logger.Infof("Getting patient %v \n", string(patientBytes))

	var patient Patient
	proto.Unmarshal(patientBytes, &patient)
	return &patient, nil
}