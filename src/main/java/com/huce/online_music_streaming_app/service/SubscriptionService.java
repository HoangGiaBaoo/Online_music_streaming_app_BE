package com.huce.online_music_streaming_app.service;

import com.huce.online_music_streaming_app.dto.PlanInfoDto;
import com.huce.online_music_streaming_app.entity.Subscription;
import com.huce.online_music_streaming_app.entity.SubscriptionPlan;
import com.huce.online_music_streaming_app.entity.User;
import com.huce.online_music_streaming_app.repository.SubscriptionRepository;
import com.huce.online_music_streaming_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public Subscription getCurrent(Long userId) {
        return subscriptionRepository
                .findFirstByUser_UserIdAndActiveTrueOrderByStartDateDesc(userId)
                .orElseGet(() -> Subscription.builder()
                        .plan(SubscriptionPlan.FREE)
                        .active(true)
                        .build());
    }

    @Transactional
    public Subscription subscribe(Long userId, SubscriptionPlan plan) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        subscriptionRepository
                .findFirstByUser_UserIdAndActiveTrueOrderByStartDateDesc(userId)
                .ifPresent(existing -> {
                    existing.setActive(false);
                    existing.setEndDate(LocalDate.now());
                    subscriptionRepository.save(existing);
                });

        Subscription sub = Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .active(true)
                .build();
        return subscriptionRepository.save(sub);
    }

    @Transactional
    public void cancel(Long userId) {
        subscriptionRepository
                .findFirstByUser_UserIdAndActiveTrueOrderByStartDateDesc(userId)
                .ifPresent(s -> {
                    s.setActive(false);
                    s.setEndDate(LocalDate.now());
                    subscriptionRepository.save(s);
                });
    }

    public List<PlanInfoDto> getPlans() {
        return List.of(
            PlanInfoDto.builder()
                .plan(SubscriptionPlan.INDIVIDUAL)
                .name("Individual")
                .priceVnd(59000L)
                .features(List.of(
                        "1 tài khoản Premium",
                        "Hủy bất cứ lúc nào",
                        "Đăng ký hoặc thanh toán một lần"))
                .build(),
            PlanInfoDto.builder()
                .plan(SubscriptionPlan.STUDENT)
                .name("Student")
                .priceVnd(29500L)
                .features(List.of(
                        "1 tài khoản Premium đã xác minh",
                        "Giảm giá cho sinh viên đủ điều kiện",
                        "Hủy bất cứ lúc nào",
                        "Đăng ký hoặc thanh toán một lần"))
                .build(),
            PlanInfoDto.builder()
                .plan(SubscriptionPlan.FAMILY)
                .name("Family")
                .priceVnd(89000L)
                .features(List.of(
                        "Tối đa 6 tài khoản Premium",
                        "Quản lý nội dung phù hợp cho trẻ em",
                        "Hủy bất cứ lúc nào"))
                .build()
        );
    }
}
