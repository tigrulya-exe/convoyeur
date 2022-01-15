package ru.nsu.convoyeur.api.execution.manager

import java.util.UUID

data class JobHandle(val id: String = UUID.randomUUID().toString()) {
    var status: JobStatus = JobStatus.NEW
}