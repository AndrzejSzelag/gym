package pl.szelag.gym.common.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/** Base class for automated auditing, capturing timestamps and operator identity. */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class AuditableEntity {

    /** timestamp when the entity record was first created */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** identifier of the user who created the record */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    /** timestamp when the entity record was last updated */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** identifier of the user who last modified the record */
    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
}