package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"fmt"
	"errors"
	"github.com/golang/protobuf/proto"
	pb "github.com/hyperledger/fabric/protos/peer"

)

type DoctorService struct {
}

func (s *DoctorService) getDoctor(stub shim.ChaincodeStubInterface, doctorId string) (*Doctor, error) {
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

	var doctor Doctor
	proto.Unmarshal(doctorBytes, &doctor)
	return &doctor, nil
}


func (s *DoctorService) createDoctor(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err error
	encodedDoctorByteString := args[0]

	doctor := Doctor{}
	err = proto.Unmarshal([]byte(encodedDoctorByteString), &doctor)
	if err != nil {
		logger.Errorf("Error while unmarshalling Doctor: %v", err.Error())
	}
	err = s.saveDoctor(stub, doctor)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

func (s *DoctorService) saveDoctor(stub shim.ChaincodeStubInterface, doctor Doctor) error {
	fmt.Printf("Saving doctor %v \n", doctor)

	doctorBytes, err := proto.Marshal(&doctor)

	doctorKey := "doctor:" + doctor.UserId
	err = stub.PutState(doctorKey, doctorBytes)
	if err != nil {
		return err
	}

	return nil
}