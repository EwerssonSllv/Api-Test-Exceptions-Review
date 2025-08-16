package com.ewersson.app_api.service.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
data class ErrorResponseDTO(val message: String)