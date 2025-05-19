package com.example.eventshub.data.model

data class ServiceEvent(
    val id: Long = 0,
    val serviceEventId:Long,
    val title: String,
    val description: String,
    val rating: Float,
    val serviceProviderId: Long,
    val fee: Float,
    val imageLink: String?,
    val serviceType: ServiceType
)