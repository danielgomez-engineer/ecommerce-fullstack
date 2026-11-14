package com.danieldev.ecommerce.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String address;
}