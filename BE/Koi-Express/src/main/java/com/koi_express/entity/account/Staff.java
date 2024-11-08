package com.koi_express.entity.account;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long staffId;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId", nullable = false)
    SystemAccount systemAccount;

}
