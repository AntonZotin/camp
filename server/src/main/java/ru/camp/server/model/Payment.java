package ru.camp.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parent;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(length = 30)
    private String method;

    @Column(length = 500)
    private String comment;

    @JsonProperty("parentId")
    public void setParentId(Long parentId) {
        if (parentId != null) {
            this.parent = new User();
            this.parent.setId(parentId);
        } else {
            this.parent = null;
        }
    }

    @JsonProperty("voucherId")
    public void setVoucherId(Long voucherId) {
        if (voucherId != null) {
            this.voucher = new Voucher();
            this.voucher.setId(voucherId);
        } else {
            this.voucher = null;
        }
    }
} 