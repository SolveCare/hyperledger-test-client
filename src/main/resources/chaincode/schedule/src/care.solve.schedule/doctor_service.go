package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"errors"
	"github.com/golang/protobuf/proto"
	"fmt"
	"encoding/json"
)

type DoctorService struct {
}

func (s *DoctorService) getAllDoctors(stub shim.ChaincodeStubInterface) ([]*DoctorPublic, error) {
	query := `{
		"selector":{
			"DoctorId":{"$regex":""}
		}
	}
	`

	resultsIterator, err := stub.GetQueryResult(query)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	logger.Infof("resultsIterator: %v", resultsIterator)

	doctors := make([]*DoctorPublic, 0)
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		logger.Infof("queryResponse: %v", queryResponse)
		if err != nil {
			return nil, err
		}

		var doctor DoctorPublic
		json.Unmarshal(queryResponse.Value, &doctor)
		logger.Infof("doctor: %v", doctor)
		doctors = append(doctors, &doctor)
	}

	return doctors, nil
}

func (s *DoctorService) getDoctorById(stub shim.ChaincodeStubInterface, doctorId string) (*DoctorPublic, error) {
	doctorKey := "doctor:" + doctorId
	doctorBytes, err := stub.GetState(doctorKey)
	if err != nil {
		return nil, err
	}
	if doctorBytes == nil {
		errorMsg := fmt.Sprintf("Doctor with key '%v' not found", doctorKey)
		logger.Errorf(errorMsg)
		return nil, errors.New(errorMsg)
	}

	logger.Infof("Getting doctor %v", string(doctorBytes))

	var doctor DoctorPublic
	json.Unmarshal(doctorBytes, &doctor)
	return &doctor, nil
}

func (s *DoctorService) saveDoctor(stub shim.ChaincodeStubInterface, doctor DoctorPublic) (*DoctorPublic, error) {
	fmt.Printf("Saving doctor %v \n", doctor)

	doctorBytes, err := json.Marshal(&doctor)

	doctorKey := "doctor:" + doctor.DoctorId
	err = stub.PutState(doctorKey, doctorBytes)
	if err != nil {
		logger.Errorf("Error while saving doctor '%v'. Error: %v", doctor, err)
		return nil, err
	}

	return &doctor, nil
}

func (s *DoctorService) decodeProtoByteString(encodedDoctorByteString string) (*DoctorPublic, error) {
	var err error

	doctor := DoctorPublic{}
	err = proto.Unmarshal([]byte(encodedDoctorByteString), &doctor)
	if err != nil {
		logger.Errorf("Error while unmarshalling Doctor: %v", err.Error())
	}

	return &doctor, err
}