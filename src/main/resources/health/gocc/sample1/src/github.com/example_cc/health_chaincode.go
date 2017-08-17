package example

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
	balance int
	insuranceId string
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

	user, err := t.getUser(stub, userName)
	if err != nil {
		return shim.Error(err.Error())
	}

	recepient, err := t.getUser(stub, recepientName)
	if err != nil {
		return shim.Error(err.Error())
	}

	if user.insuranceId != "" {
		recepient.balance += amount
		user.balance -= amount

		return shim.Success(nil)
	} else {
		insurance, err := t.getUser(stub, user.insuranceId)
		if err != nil {
			return shim.Error(err.Error())
		}

		halfAmount := amount / 2

		recepient.balance += amount
		user.balance -= halfAmount
		insurance.balance -= halfAmount

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

	if user.insuranceId != "" {
		return shim.Error("User " + userName + " already has insurance " + user.insuranceId)
	} else {
		user.insuranceId = insrance
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
		"\"Balance\":\"" + string(user.balance) + "\"," +
		"\"AccountId\":" + user.insuranceId +
		"}"
	fmt.Printf("Query Response:%s\n", jsonResp)

	return shim.Success([]byte(jsonResp))
}

func (t *SimpleChaincode) getUser(stub shim.ChaincodeStubInterface, userName string) (*User, error) {
	userBytes, err := stub.GetState(userName)

	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + userName + "\"}"
		return nil, errors.New(jsonResp)
	}

	if userBytes == nil {
		jsonResp := "{\"Error\":\"Nil user info for " + userName + "\"}"
		return nil, errors.New(jsonResp)
	}

	var user User
	json.Unmarshal(userBytes, user)

	return &user, nil
}

func (t *SimpleChaincode) createUser(stub shim.ChaincodeStubInterface) pb.Response {
	var userName string
	var initialCoins int
	var err error

	_, args := stub.GetFunctionAndParameters()

	userName = args[1]
	initialCoins, err = strconv.Atoi(args[2])

	if err != nil {
		return shim.Error("Expecting integer value for asset holding")
	}

	user := User{initialCoins, ""}

	jsonUser, err := json.Marshal(user)
	err = stub.PutState(userName, jsonUser)
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