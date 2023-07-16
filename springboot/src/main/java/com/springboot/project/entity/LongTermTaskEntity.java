package com.springboot.project.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Since the timeout period of the connection request of the cloud server is
 * limited, this class is added to return the result of this situation.
 * 
 * @author zdu
 *
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
public class LongTermTaskEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Date createDate;

    /**
     * When the update time exceeds one minute, it means that the task is
     * interrupted.
     */
    @Column(nullable = false)
    private Date updateDate;

    /**
     * Is it running or has ended
     */
    @Column(nullable = false)
    private Boolean isDone;

    /**
     * Stored is the json string
     */
    @Column(nullable = true, length = 4000)
    private String result;

}
