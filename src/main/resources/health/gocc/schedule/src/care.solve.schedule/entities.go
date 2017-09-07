package main

type Patient struct {
	UserId    string
	Email     string
	FirstName string
	LastName  string
	Balance   float64
}

type Doctor struct {
	UserId    string
	Email     string
	FirstName string
	LastName  string
	Level     string
}

type Schedule struct {
	ScheduleId string
	DoctorId   string
	Records    map[string]ScheduleRecord
}

type Slot struct {
	TimeStart  uint64
	TimeFinish uint64
}

type ScheduleRecord struct {
	RecordId    string
	Description string
	PatientId   string
	Slot        Slot
}
