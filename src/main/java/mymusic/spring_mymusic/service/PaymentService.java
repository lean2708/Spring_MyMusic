package mymusic.spring_mymusic.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import mymusic.spring_mymusic.configuration.VNPAYConfig;
import mymusic.spring_mymusic.dto.request.PaymentCallbackRequest;
import mymusic.spring_mymusic.dto.response.PremiumResponse;
import mymusic.spring_mymusic.dto.response.VNPayResponse;
import mymusic.spring_mymusic.entity.Role;
import mymusic.spring_mymusic.entity.User;
import mymusic.spring_mymusic.exception.ErrorCode;
import mymusic.spring_mymusic.exception.AppException;
import mymusic.spring_mymusic.repository.RoleRepository;
import mymusic.spring_mymusic.repository.UserRepository;
import mymusic.spring_mymusic.util.VNPayUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService {

    private final VNPAYConfig vnPayConfig;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM')")
    public VNPayResponse createVnPayPayment(String premiumType, HttpServletRequest request) {
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();

        long amount = 100;

        switch (premiumType) {
            case "1-month":
                amount *=  30000L;
                break;
            case "3-month":
                amount *= 79000L;
                break;
            case "6-month":
                amount *= 169000L;
                break;
            case "12-month":
                amount *= 349000L;
                break;
            default:
                throw new AppException(ErrorCode.INVALID_PREMIUM_TYPE);
        }

        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));

        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        // Tao chuoi da ma hoa
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);

        // Tao chuoi chua ma hoa
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        // Thêm vnp_SecureHash vào URL
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        // Tao URL Final
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return VNPayResponse.builder()
                .code("ok")
                .message("Mã thanh toán đã được tạo thành công. Bạn sẽ được chuyển đến cổng thanh toán để hoàn tất giao dịch.")
                .paymentUrl(paymentUrl).build();
    }

    public PremiumResponse updatePremium(PaymentCallbackRequest request){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        LocalDate expiryDate = user.getPremiumExpiryDate();

        long amount = request.getAmount() / 100;

        switch ((int) amount) {
            case 30000:
                expiryDate = (expiryDate != null) ? expiryDate.plusMonths(1)
                        : LocalDate.now().plusMonths(1);
                break;
            case 79000:
                expiryDate = (expiryDate != null) ? expiryDate.plusMonths(3)
                        : LocalDate.now().plusMonths(3);
                break;
            case 169000:
                expiryDate = (expiryDate != null) ? expiryDate.plusMonths(6)
                        : LocalDate.now().plusMonths(6);
                break;
            case 349000:
                expiryDate = (expiryDate != null) ? expiryDate.plusYears(1)
                        : LocalDate.now().plusMonths(12);
                break;
            default:
                throw new AppException(ErrorCode.INVALID_PREMIUM_TYPE);
        }

        Role premiumRole = roleRepository.findByName("PREMIUM").orElseThrow(
                () -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        Set<Role> roles = new HashSet<>();
        roles.add(premiumRole);
        user.setRoles(roles);

        user.setPremiumStatus(true);
        user.setPremiumExpiryDate(expiryDate);

        userRepository.save(user);

        emailService.sendUserEmailWithPayment(user);

        return  new PremiumResponse().builder()
                .isPremiumStatus(true)
                .expirationDate(expiryDate)
                .build();
    }

    public PremiumResponse checkPremiumStatus(HttpServletRequest request){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isPremiumStatus = user.isPremiumStatus();
        return PremiumResponse.builder()
                .isPremiumStatus(isPremiumStatus)
                .expirationDate(user.getPremiumExpiryDate())
                .build();
    }

    @Scheduled(fixedRate = 3600000)
    @Async
    public void checkPremiumExpiry() {
        LocalDate currentDate = LocalDate.now();
        List<User> listUser = userRepository.findAllByRoles_Name("PREMIUM");

        for (User user : listUser) {
            if(user.getPremiumExpiryDate() != null && user.getPremiumExpiryDate().isBefore(currentDate)){
                user.setPremiumStatus(false);
                user.setPremiumExpiryDate(null);

                Role userRole = roleRepository.findByName("USER").orElseThrow(
                        () -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
                Set<Role> roles = new HashSet<>();
                roles.add(userRole);
                user.setRoles(roles);

                userRepository.save(user);
            }
        }
    }
}
