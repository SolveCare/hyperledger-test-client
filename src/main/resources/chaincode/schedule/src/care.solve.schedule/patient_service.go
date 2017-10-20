package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/golang/protobuf/proto"
	"encoding/json"
)

type PatientService struct {

}

func (t *PatientService) decodeProtoByteString(encodedPatientByteString string) (*PatientPublic, error) {
	var err error

	patient := PatientPublic{}
	err = proto.Unmarshal([]byte(encodedPatientByteString), &patient)
	if err != nil {
		logger.Errorf("Error while unmarshalling Patient: %v", err.Error())
	}

	return &patient, err
}

func (t *PatientService) savePatient(stub shim.ChaincodeStubInterface, patient PatientPublic) (*PatientPublic, error) {
	logger.Infof("Saving patient %v", patient)

	jsonUser, err := json.Marshal(&patient)

	patientKey := "patient:" + patient.PatientId

	err = stub.PutState(patientKey, jsonUser)
	if err != nil {
		logger.Errorf("Error while saving patient '%v'. Error: %v", patient, err)
		return nil, err
	}

	return &patient, nil
}

func (t *PatientService) getPatientById(stub shim.ChaincodeStubInterface, patientId string) (*PatientPublic, error) {

	patientKey := "patient:" + patientId
	patientBytes, err := stub.GetState(patientKey)
	if err != nil {
		logger.Errorf("Error while getting patient with key '%v'. Error: %v", patientKey, err)
		return nil, err
	}

	logger.Infof("Getting patient %v \n", string(patientBytes))

	var patient PatientPublic
	json.Unmarshal(patientBytes, &patient)
	return &patient, nil
}