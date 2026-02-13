package com.kasthurisweets.backend.controller;

import com.kasthurisweets.backend.service.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ✅ USER: Cash on Delivery
    @PostMapping("/cod/{orderId}")
    public String payByCOD(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        String email = authentication.getName(); // 🔐 from JWT
        paymentService.payByCOD(orderId, email);
        return "Cash on Delivery selected successfully";
    }
}
