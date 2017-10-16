package care.solve.backend.controller;

import care.solve.backend.entity.Doctor;
import care.solve.backend.entity.Schedule;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.entity.ScheduleRecord;
import care.solve.backend.entity.Slot;
import care.solve.backend.service.TransactionService;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private TransactionService transactionService;
    private HFClient client;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;

    public DoctorController(TransactionService transactionService, HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer) {
        this.transactionService = transactionService;
        this.client = client;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
    }

    @PostMapping
    public void create(@RequestBody Doctor doctor) {
        transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "createDoctor",
                new String[]{doctor.getId(), doctor.getEmail(), doctor.getFirstName(), doctor.getLastName(), doctor.getLevel()});


    }

    @GetMapping("{doctorId}/schedule")
    public Schedule getSchedule(@PathVariable String doctorId) {
        ScheduleProtos.Schedule protoSchedule = (ScheduleProtos.Schedule)transactionService.sendQueryTransaction(
                client,
                chaincodeId,
                healthChannel,
                "getDoctorsSchedule",
                new String[]{doctorId});

        Map<String, ScheduleProtos.ScheduleRecord> protoScheduleRecords = protoSchedule.getRecordsMap();

        Map<String, ScheduleRecord> scheduleRecordMap = protoScheduleRecords.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> {
                            ScheduleProtos.ScheduleRecord protoRecord = e.getValue();
                            ScheduleProtos.Slot protoSlot = protoRecord.getSlot();

                            Slot slot = Slot.builder()
                                    .timeEnd(protoSlot.getTimeFinish())
                                    .timeStart(protoSlot.getTimeStart())
                                    .build();

                            ScheduleRecord scheduleRecord = ScheduleRecord.builder()
                                    .description(protoRecord.getDescription())
                                    .patientId(protoRecord.getPatientId())
                                    .recordId(protoRecord.getRecordId())
                                    .slot(slot)
                                    .build();

                            return scheduleRecord;
                        }
                ));

        return Schedule.builder()
                .doctorId(protoSchedule.getDoctorId())
                .scheduleId(protoSchedule.getScheduleId())
                .records(scheduleRecordMap)
                .build();
    }
}
