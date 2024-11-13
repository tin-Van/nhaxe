package com.poly.entity;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;  // Đừng quên import annotation này
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fullname", columnDefinition = "nvarchar(255)")
    @NotEmpty(message = "Vui lòng điền thông tin đầy đủ")
    private String fullname;

    @Column(name = "username", unique = true)
    @NotEmpty(message = "Vui lòng điền thông tin đầy đủ")
    private String username;

    @Column(name = "password")
    @JsonIgnore // Ẩn giá trị mật khẩu khi trả về dữ liệu từ API
    @NotEmpty(message = "Vui lòng điền thông tin đầy đủ")
    private String password;

    @Column(name = "gender")
    @NotNull(message = "Vui lòng điền thông tin đầy đủ")
    private Boolean gender;

    @Column(name = "email", unique = true)
    @NotEmpty(message = "Vui lòng điền thông tin đầy đủ")
    private String email;

    @Column(name = "phone")
    @NotEmpty(message = "Vui lòng điền thông tin đầy đủ")
    private String phone;

    @NotEmpty
    @Column(name = "role")
    private String role;

    @Column(name = "avatar")
    private String avatar;
    
    @Column(name = "created_Date")
    private Timestamp createdDate;


	public void setToken(String verificationCode) {
		// TODO Auto-generated method stub
		// Xử lý logic nếu cần
	}

	public void setCreatedDate(java.util.Date date) {
	        if (date != null) {
	            this.createdDate = new java.sql.Timestamp(date.getTime());
	        }
	    }

}
