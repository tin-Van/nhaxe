package com.poly.model;

import java.time.LocalDateTime;

import org.apache.catalina.User;

import com.poly.entity.Account;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = Account.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "username")
    private Account account;

    private LocalDateTime expiryDate;



    public PasswordResetToken(String token, Account account) {
        this.token = token;
        this.account = account;
        this.expiryDate = LocalDateTime.now().plusMinutes(2); // Token hết hạn sau 24 giờ
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    // Getter và setter...
}