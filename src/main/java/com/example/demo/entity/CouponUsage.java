package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_usages")
public class CouponUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_usage_id")
    private Integer couponUsageId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "coupon_id", nullable = false)
    private Integer couponId;

    @Column(name = "order_id", unique = true)
    private Integer orderId;

    @Column(name = "discount_code", length = 50)
    private String discountCode;

    @Column(name = "discount_amount")
    private Integer discountAmount;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

    public CouponUsage() {
    }

    public Integer getCouponUsageId() {
        return couponUsageId;
    }

    public void setCouponUsageId(Integer couponUsageId) {
        this.couponUsageId = couponUsageId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
}
