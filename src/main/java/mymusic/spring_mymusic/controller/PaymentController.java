package mymusic.spring_mymusic.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import mymusic.spring_mymusic.dto.request.PaymentCallbackRequest;
import mymusic.spring_mymusic.dto.response.ApiResponse;
import mymusic.spring_mymusic.dto.response.PremiumResponse;
import mymusic.spring_mymusic.dto.response.SongResponse;
import mymusic.spring_mymusic.dto.response.VNPayResponse;
import mymusic.spring_mymusic.service.PaymentService;


@RequestMapping("/v1/payment")
@Validated
@Slf4j(topic = "PAYMENT-CONTROLLER")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/vn-pay")
    public ApiResponse<VNPayResponse> pay(@RequestParam @NotBlank String premiumType, HttpServletRequest request) {
        return new ApiResponse<>(HttpStatus.OK.value(),
                "Tạo thành công URL thanh toán VNPay",
                paymentService.createVnPayPayment(premiumType, request));
    }

    @PostMapping("/vn-pay-callback")
    public ApiResponse<PremiumResponse> payCallbackHandler(@RequestBody PaymentCallbackRequest request) {
        String status = request.getResponseCode();
        if (status.equals("00")) {
            return new ApiResponse<>(1000,
                    "Thanh toán thành công",
                    paymentService.updatePremium(request));
        } else {
            log.error("Thanh toán không thành công với mã phản hồi: " + status);
            return new ApiResponse<>(4000, "Thanh toán thất bại", null);
        }
    }

    @GetMapping("/premium-status")
    public ApiResponse<PremiumResponse> checkPremiumStatus(HttpServletRequest request){
        return ApiResponse.<PremiumResponse>builder()
                .code(HttpStatus.OK.value())
                .result(paymentService.checkPremiumStatus(request))
                .message("Premium Status")
                .build();
    }
}
