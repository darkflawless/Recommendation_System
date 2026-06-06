
package com.example.core.customer;

import com.example.core.auth.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "customers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends User {

    @Column(name = "username", nullable = false, length = 255, unique = true) 
    private String username;

    @Column(name = "mem_status", length = 50)
    private String memStatus;


}
