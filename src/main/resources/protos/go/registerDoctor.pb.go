// Code generated by protoc-gen-go. DO NOT EDIT.
// source: registerDoctor.proto

/*
Package main is a generated protocol buffer package.

It is generated from these files:
	registerDoctor.proto

It has these top-level messages:
	Patient
	Doctor
	Schedule
	Slot
	ScheduleRecord
*/
package main

import proto "github.com/golang/protobuf/proto"
import fmt "fmt"
import math "math"

// Reference imports to suppress errors if they are not otherwise used.
var _ = proto.Marshal
var _ = fmt.Errorf
var _ = math.Inf

// This is a compile-time assertion to ensure that this generated file
// is compatible with the proto package it is being compiled against.
// A compilation error at this line likely means your copy of the
// proto package needs to be updated.
const _ = proto.ProtoPackageIsVersion2 // please upgrade the proto package

type Patient struct {
	UserId    string  `protobuf:"bytes,1,opt,name=UserId" json:"UserId,omitempty"`
	Email     string  `protobuf:"bytes,2,opt,name=Email" json:"Email,omitempty"`
	FirstName string  `protobuf:"bytes,3,opt,name=FirstName" json:"FirstName,omitempty"`
	LastName  string  `protobuf:"bytes,4,opt,name=LastName" json:"LastName,omitempty"`
	Balance   float32 `protobuf:"fixed32,5,opt,name=Balance" json:"Balance,omitempty"`
}

func (m *Patient) Reset()                    { *m = Patient{} }
func (m *Patient) String() string            { return proto.CompactTextString(m) }
func (*Patient) ProtoMessage()               {}
func (*Patient) Descriptor() ([]byte, []int) { return fileDescriptor0, []int{0} }

func (m *Patient) GetUserId() string {
	if m != nil {
		return m.UserId
	}
	return ""
}

func (m *Patient) GetEmail() string {
	if m != nil {
		return m.Email
	}
	return ""
}

func (m *Patient) GetFirstName() string {
	if m != nil {
		return m.FirstName
	}
	return ""
}

func (m *Patient) GetLastName() string {
	if m != nil {
		return m.LastName
	}
	return ""
}

func (m *Patient) GetBalance() float32 {
	if m != nil {
		return m.Balance
	}
	return 0
}

type Doctor struct {
	UserId    string `protobuf:"bytes,1,opt,name=UserId" json:"UserId,omitempty"`
	Email     string `protobuf:"bytes,2,opt,name=Email" json:"Email,omitempty"`
	FirstName string `protobuf:"bytes,3,opt,name=FirstName" json:"FirstName,omitempty"`
	LastName  string `protobuf:"bytes,4,opt,name=LastName" json:"LastName,omitempty"`
	Level     string `protobuf:"bytes,5,opt,name=Level" json:"Level,omitempty"`
}

func (m *Doctor) Reset()                    { *m = Doctor{} }
func (m *Doctor) String() string            { return proto.CompactTextString(m) }
func (*Doctor) ProtoMessage()               {}
func (*Doctor) Descriptor() ([]byte, []int) { return fileDescriptor0, []int{1} }

func (m *Doctor) GetUserId() string {
	if m != nil {
		return m.UserId
	}
	return ""
}

func (m *Doctor) GetEmail() string {
	if m != nil {
		return m.Email
	}
	return ""
}

func (m *Doctor) GetFirstName() string {
	if m != nil {
		return m.FirstName
	}
	return ""
}

func (m *Doctor) GetLastName() string {
	if m != nil {
		return m.LastName
	}
	return ""
}

func (m *Doctor) GetLevel() string {
	if m != nil {
		return m.Level
	}
	return ""
}

type Schedule struct {
	ScheduleId string                     `protobuf:"bytes,1,opt,name=ScheduleId" json:"ScheduleId,omitempty"`
	DoctorId   string                     `protobuf:"bytes,2,opt,name=DoctorId" json:"DoctorId,omitempty"`
	Records    map[string]*ScheduleRecord `protobuf:"bytes,3,rep,name=Records" json:"Records,omitempty" protobuf_key:"bytes,1,opt,name=key" protobuf_val:"bytes,2,opt,name=value"`
}

func (m *Schedule) Reset()                    { *m = Schedule{} }
func (m *Schedule) String() string            { return proto.CompactTextString(m) }
func (*Schedule) ProtoMessage()               {}
func (*Schedule) Descriptor() ([]byte, []int) { return fileDescriptor0, []int{2} }

func (m *Schedule) GetScheduleId() string {
	if m != nil {
		return m.ScheduleId
	}
	return ""
}

func (m *Schedule) GetDoctorId() string {
	if m != nil {
		return m.DoctorId
	}
	return ""
}

func (m *Schedule) GetRecords() map[string]*ScheduleRecord {
	if m != nil {
		return m.Records
	}
	return nil
}

type Slot struct {
	TimeStart  uint64 `protobuf:"varint,1,opt,name=TimeStart" json:"TimeStart,omitempty"`
	TimeFinish uint64 `protobuf:"varint,2,opt,name=TimeFinish" json:"TimeFinish,omitempty"`
}

func (m *Slot) Reset()                    { *m = Slot{} }
func (m *Slot) String() string            { return proto.CompactTextString(m) }
func (*Slot) ProtoMessage()               {}
func (*Slot) Descriptor() ([]byte, []int) { return fileDescriptor0, []int{3} }

func (m *Slot) GetTimeStart() uint64 {
	if m != nil {
		return m.TimeStart
	}
	return 0
}

func (m *Slot) GetTimeFinish() uint64 {
	if m != nil {
		return m.TimeFinish
	}
	return 0
}

type ScheduleRecord struct {
	RecordId    string `protobuf:"bytes,1,opt,name=RecordId" json:"RecordId,omitempty"`
	Description string `protobuf:"bytes,2,opt,name=Description" json:"Description,omitempty"`
	PatientId   string `protobuf:"bytes,3,opt,name=PatientId" json:"PatientId,omitempty"`
	Slot        *Slot  `protobuf:"bytes,4,opt,name=Slot" json:"Slot,omitempty"`
}

func (m *ScheduleRecord) Reset()                    { *m = ScheduleRecord{} }
func (m *ScheduleRecord) String() string            { return proto.CompactTextString(m) }
func (*ScheduleRecord) ProtoMessage()               {}
func (*ScheduleRecord) Descriptor() ([]byte, []int) { return fileDescriptor0, []int{4} }

func (m *ScheduleRecord) GetRecordId() string {
	if m != nil {
		return m.RecordId
	}
	return ""
}

func (m *ScheduleRecord) GetDescription() string {
	if m != nil {
		return m.Description
	}
	return ""
}

func (m *ScheduleRecord) GetPatientId() string {
	if m != nil {
		return m.PatientId
	}
	return ""
}

func (m *ScheduleRecord) GetSlot() *Slot {
	if m != nil {
		return m.Slot
	}
	return nil
}

func init() {
	proto.RegisterType((*Patient)(nil), "main.Patient")
	proto.RegisterType((*Doctor)(nil), "main.Doctor")
	proto.RegisterType((*Schedule)(nil), "main.Schedule")
	proto.RegisterType((*Slot)(nil), "main.Slot")
	proto.RegisterType((*ScheduleRecord)(nil), "main.ScheduleRecord")
}

func init() { proto.RegisterFile("registerDoctor.proto", fileDescriptor0) }

var fileDescriptor0 = []byte{
	// 395 bytes of a gzipped FileDescriptorProto
	0x1f, 0x8b, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0xff, 0xbc, 0x52, 0xcd, 0x8a, 0xdb, 0x30,
	0x10, 0x46, 0xb1, 0x9d, 0x6c, 0xc6, 0xa5, 0x14, 0x61, 0x8a, 0xd9, 0x96, 0xc5, 0xb8, 0x97, 0xd0,
	0x83, 0x0f, 0x29, 0x85, 0xd2, 0x63, 0xc8, 0x2e, 0x04, 0x96, 0x12, 0x94, 0xf6, 0x01, 0x54, 0x7b,
	0xe8, 0x8a, 0xca, 0x56, 0x90, 0xb5, 0x4b, 0xf3, 0x02, 0xbd, 0xb4, 0xef, 0xd6, 0x57, 0x2a, 0xfa,
	0xb1, 0xe3, 0xbc, 0xc0, 0xde, 0xf4, 0x7d, 0x9f, 0x34, 0xf3, 0xcd, 0xa7, 0x81, 0x4c, 0xe3, 0x0f,
	0xd1, 0x1b, 0xd4, 0x5b, 0x55, 0x1b, 0xa5, 0xab, 0xa3, 0x56, 0x46, 0xd1, 0xb8, 0xe5, 0xa2, 0x2b,
	0xff, 0x10, 0x58, 0xec, 0xb9, 0x11, 0xd8, 0x19, 0xfa, 0x1a, 0xe6, 0xdf, 0x7a, 0xd4, 0xbb, 0x26,
	0x27, 0x05, 0x59, 0x2d, 0x59, 0x40, 0x34, 0x83, 0xe4, 0xb6, 0xe5, 0x42, 0xe6, 0x33, 0x47, 0x7b,
	0x40, 0xdf, 0xc2, 0xf2, 0x4e, 0xe8, 0xde, 0x7c, 0xe1, 0x2d, 0xe6, 0x91, 0x53, 0xce, 0x04, 0xbd,
	0x86, 0xab, 0x7b, 0x1e, 0xc4, 0xd8, 0x89, 0x23, 0xa6, 0x39, 0x2c, 0x36, 0x5c, 0xf2, 0xae, 0xc6,
	0x3c, 0x29, 0xc8, 0x6a, 0xc6, 0x06, 0x58, 0xfe, 0x26, 0x30, 0xf7, 0x26, 0x9f, 0xcd, 0x4c, 0x06,
	0xc9, 0x3d, 0x3e, 0xa1, 0x74, 0x56, 0x96, 0xcc, 0x83, 0xf2, 0x1f, 0x81, 0xab, 0x43, 0xfd, 0x80,
	0xcd, 0xa3, 0x44, 0x7a, 0x03, 0x30, 0x9c, 0x47, 0x3b, 0x13, 0xc6, 0x96, 0xf7, 0xa6, 0x77, 0x4d,
	0x70, 0x35, 0x62, 0xfa, 0x11, 0x16, 0x0c, 0x6b, 0xa5, 0x9b, 0x3e, 0x8f, 0x8a, 0x68, 0x95, 0xae,
	0xdf, 0x54, 0x36, 0xf7, 0x6a, 0x78, 0x5e, 0x05, 0xf5, 0xb6, 0x33, 0xfa, 0xc4, 0x86, 0xbb, 0xd7,
	0x7b, 0x78, 0x31, 0x15, 0xe8, 0x2b, 0x88, 0x7e, 0xe2, 0x29, 0xf4, 0xb6, 0x47, 0xfa, 0x1e, 0x92,
	0x27, 0x2e, 0x1f, 0xd1, 0x75, 0x4c, 0xd7, 0xd9, 0x65, 0x59, 0xff, 0x98, 0xf9, 0x2b, 0x9f, 0x67,
	0x9f, 0x48, 0xb9, 0x85, 0xf8, 0x20, 0x95, 0xb1, 0x49, 0x7d, 0x15, 0x2d, 0x1e, 0x0c, 0xd7, 0xc6,
	0xd5, 0x8b, 0xd9, 0x99, 0xb0, 0xa3, 0x5a, 0x70, 0x27, 0x3a, 0xd1, 0x3f, 0xb8, 0xd2, 0x31, 0x9b,
	0x30, 0xe5, 0x5f, 0x02, 0x2f, 0x2f, 0x7b, 0xd8, 0xe9, 0xfd, 0x69, 0xcc, 0x66, 0xc4, 0xb4, 0x80,
	0x74, 0x8b, 0x7d, 0xad, 0xc5, 0xd1, 0x08, 0xd5, 0x85, 0x70, 0xa6, 0x94, 0xb5, 0x13, 0xd6, 0x6f,
	0xd7, 0x0c, 0x1f, 0x37, 0x12, 0xf4, 0xc6, 0x9b, 0x76, 0x9f, 0x96, 0xae, 0x21, 0xcc, 0x28, 0x95,
	0x61, 0x8e, 0xdf, 0xbc, 0x83, 0xb4, 0x56, 0x6d, 0x85, 0xbf, 0x78, 0x7b, 0x94, 0xb8, 0xc9, 0x7c,
	0xec, 0x2c, 0xac, 0xfb, 0xde, 0xee, 0x79, 0xff, 0x7d, 0xee, 0xf6, 0xfd, 0xc3, 0xff, 0x00, 0x00,
	0x00, 0xff, 0xff, 0x41, 0x72, 0xfb, 0x03, 0x07, 0x03, 0x00, 0x00,
}