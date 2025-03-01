package com.nainesh.accounts.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Accounts extends BaseEntity{

    @Column(name="customer_id") //Not required as convention is followed
    private Long customerId;

    @Id
    private Long accountNumber; //Generate this using custom logic

    private String accountType ;

    private String branchAddress;
}
