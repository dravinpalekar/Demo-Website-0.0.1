package dravin.com.repository.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract  class AbstractDateTimeEntity {

    @Column(name="created_at", nullable = false)
    @CreationTimestamp
    protected Date createdAt;

    @Column(name="updated_at", nullable = false)
    @UpdateTimestamp
    protected Date updatedAt;

    @Column(name="deleted_at", nullable = true)
    protected Date deletedAt;
}
