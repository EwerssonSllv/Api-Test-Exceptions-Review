package com.ewersson.app_api.model.dashboard

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table
data class Dashboard(

    @Id
    @Column("id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: String



)