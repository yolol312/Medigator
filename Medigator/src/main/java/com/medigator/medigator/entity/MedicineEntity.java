// MedicineEntity.java

package com.medigator.medigator.entity;

import com.medigator.medigator.dto.MedicineDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.convert.DataSizeUnit;

import java.time.LocalDate;
import java.util.Objects;

// MedicineEntity.java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Medicine_table")
public class MedicineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Column
    private String entpName;

    @Column
    private String itemName;

    @Column(length = 1000)
    private String efcyQesitm;

    @Column(length = 1000)
    private String atpnQesitm;

    @Column(length = 1000)
    private String useMethodQesitm;


    @Column(length = 1000)
    private String intrcQesitm;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicineEntity)) return false;
        MedicineEntity that = (MedicineEntity) o;
        return Objects.equals(getItemName(), that.getItemName()) &&
                Objects.equals(getEntpName(), that.getEntpName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItemName(), getEntpName());
    }
}