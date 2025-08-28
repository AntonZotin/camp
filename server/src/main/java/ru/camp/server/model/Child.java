package ru.camp.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "children")
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private User parent;

    @JsonProperty("parentId")
    public Long getParentId() {
        return (parent != null) ? parent.getId() : null;
    }

    @JsonProperty("parentId")
    public void setParentId(Long parentId) {
        if (parentId != null) {
            this.parent = new User();
            this.parent.setId(parentId);
        } else {
            this.parent = null;
        }
    }

    @JsonProperty("parentUsername")
    public String getParentUsername() {
        return (parent != null) ? parent.getUsername() : null;
    }
} 