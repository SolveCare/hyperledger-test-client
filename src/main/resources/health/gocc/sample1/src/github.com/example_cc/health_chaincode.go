package main

import (
	"fmt"
	"strconv"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"

	"encoding/json"
	"errors"
)

type SimpleChaincode struct {
}

type User struct {
	Balance int `json:"Balance"`
	InsuranceId string `json:"InsuranceId"`
	Key string `json:"Key"`
}

// Init initializes the chaincode state
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
    if transientMap, err := stub.GetTransient(); err == nil {
    		if transientData, ok := transientMap["result"]; ok {
    			return shim.Success(transientData)
    		}
    	}

	return shim.Success(nil)
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()

	fmt.Printf("Invoke function %v with args %v  \n", function, args)

	if function != "invoke" {
		return shim.Error("Unknown function call")
	}

	if args[0] == "createUser" {
		return t.createUser(stub)
	}

	if args[0] == "query" {
		return t.query(stub)
	}

	if args[0] == "addInsurance" {
		return t.addInsurance(stub)
	}

	if args[0] == "pay" {
		return t.pay(stub)
	}

	return shim.Error("Unknown action, check the first argument")
}

func (t *SimpleChaincode) pay(stub shim.ChaincodeStubInterface) pb.Response {
	_, args := stub.GetFunctionAndParameters()
	var userName string
	var recepientName string
	var amount int
	var err error

	userName = args[1]
	recepientName = args[2]
	amount, err = strconv.Atoi(args[3])

	fmt.Println("Transfer " + strconv.Itoa(amount) + " points from " + userName + " to " + recepientName)

	user, err := t.getUser(stub, userName)
	if err != nil {
		return shim.Error(err.Error())
	}

	recepient, err := t.getUser(stub, recepientName)
	if err != nil {
		return shim.Error(err.Error())
	}

	if user.InsuranceId == "" {
		recepient.Balance += amount
		user.Balance -= amount

		err = t.saveUser(stub, userName, *user)
		if err != nil {
			return shim.Error(err.Error())
		}

		err = t.saveUser(stub, recepientName, *recepient)
		if err != nil {
			return shim.Error(err.Error())
		}

		return shim.Success(nil)
	} else {
		insurance, err := t.getUser(stub, user.InsuranceId)
		if err != nil {
			return shim.Error(err.Error())
		}

		halfAmount := amount / 2

		recepient.Balance += amount
		user.Balance -= halfAmount
		insurance.Balance -= halfAmount

		err = t.saveUser(stub, userName, *user)
		if err != nil {
			return shim.Error(err.Error())
		}

		err = t.saveUser(stub, recepientName, *recepient)
		if err != nil {
			return shim.Error(err.Error())
		}

		err = t.saveUser(stub, insurance.Key, *insurance)
		if err != nil {
			return shim.Error(err.Error())
		}

		return shim.Success(nil)
	}
}

func (t *SimpleChaincode) addInsurance(stub shim.ChaincodeStubInterface) pb.Response {
	_, args := stub.GetFunctionAndParameters()
	var userName string
	var insrance string
	var err error

	userName = args[1]
	insrance = args[2]

	var user *User
	user, err = t.getUser(stub, userName)

	if err != nil {
		return shim.Error(err.Error())
	}

	if user.InsuranceId != "" {
		return shim.Error("User " + userName + " already has insurance " + user.InsuranceId)
	} else {
		user.InsuranceId = insrance
		jsonUser, err := json.Marshal(user)

		if err != nil {
			return shim.Error(err.Error())
		}

		stub.PutState(userName, jsonUser)
		return shim.Success(nil)
	}
}

func (t *SimpleChaincode) query(stub shim.ChaincodeStubInterface) pb.Response {

	_, args := stub.GetFunctionAndParameters()

	var userToQuery string
	var err error

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
	}

	userToQuery = args[1]

	var user *User
	user, err = t.getUser(stub, userToQuery)

	if err != nil {
		return shim.Error(err.Error())
	}

	jsonResp := "{" +
		"\"Name\":\"" + userToQuery + "\"," +
		"\"Balance\":\"" + strconv.Itoa(user.Balance) + "\"," +
		"\"AccountId\":" + user.InsuranceId + "\"," +
		"\"Key\":" + user.Key + "\"," +
		"}"
	fmt.Printf("Query Response:%s\n", jsonResp)

	return shim.Success([]byte(jsonResp))
}

func (t *SimpleChaincode) getUser(stub shim.ChaincodeStubInterface, userName string) (*User, error) {
	userBytes, err := stub.GetState(userName)

	fmt.Println("Getting user " + userName)

	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + userName + "\"}"
		return nil, errors.New(jsonResp)
	}

	if userBytes == nil {
		jsonResp := "{\"Error\":\"Nil user info for " + userName + "\"}"
		return nil, errors.New(jsonResp)
	}

	var user User
	json.Unmarshal(userBytes, &user)

	fmt.Printf("User -> %v \n", user)

	return &user, nil
}

func (t *SimpleChaincode) saveUser(stub shim.ChaincodeStubInterface, userName string, user User) error {
	fmt.Printf("Saving user %v \n", user)

	jsonUser, err := json.Marshal(user)
	err = stub.PutState(userName, jsonUser)
	if err != nil {
		return err
	}

	return nil
}


func (t *SimpleChaincode) createUser(stub shim.ChaincodeStubInterface) pb.Response {
	var userName string
	var initialCoins int
	var err error

	_, args := stub.GetFunctionAndParameters()

	userName = args[1]
	initialCoins, err = strconv.Atoi(args[2])

	fmt.Println("Creating user " + userName + " with initial amount " + strconv.Itoa(initialCoins))

	if err != nil {
		return shim.Error("Expecting integer value for asset holding")
	}

	user := User{initialCoins, "", userName}

	err = t.saveUser(stub, userName, user)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}