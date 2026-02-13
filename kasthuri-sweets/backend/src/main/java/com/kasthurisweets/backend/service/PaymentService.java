package com.kasthurisweets.backend.service;

public interface PaymentService {

    void payByCOD(Long orderId, String email);
}
