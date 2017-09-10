package main

type DateRange interface {
	GetTimestampStart() int64
	GetTimestampFinish() int64
}
