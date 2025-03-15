package com.nainesh.accounts.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="customer_id") //Not required as convention is followed
    private Long customerId;

    private String name;

    private String email;

    private String mobileNumber;
}
