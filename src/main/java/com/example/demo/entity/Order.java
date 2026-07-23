package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "order_date", nullable = false, insertable = false, updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "subtotal", nullable = false)
    private Integer subtotal;

    @Column(name = "shipping_fee", nullable = false)
    private Integer shippingFee;

    @Column(name = "tax", nullable = false)
    private Integer tax;

    @Column(name = "coupon_discount", nullable = false)
    private Integer couponDiscount;

    @Column(name = "points_used", nullable = false)
    private Integer pointsUsed;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 取得した値をMyBatisが設定する(JPAには@Transientで永続化対象外にする)。
     */
    @Transient
    private String memberName;

    /**
     * 管理者向け注文詳細取得時にServiceが設定する(JPAには@Transientで永続化対象外にする)。
     */
    @Transient
    private List<OrderItem> orderItems;

    public Order() {
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public Integer getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(Integer shippingFee) {
        this.shippingFee = shippingFee;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public Integer getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(Integer couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public Integer getPointsUsed() {
        return pointsUsed;
    }

    public void setPointsUsed(Integer pointsUsed) {
        this.pointsUsed = pointsUsed;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
