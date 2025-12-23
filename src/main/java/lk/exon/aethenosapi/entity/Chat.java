package lk.exon.aethenosapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "chat")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "message", length = 1000)
    private String message;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "from_gup_id", nullable = false)
    private GeneralUserProfile fromGeneralUserProfile;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "to_gup_id", nullable = false)
    private GeneralUserProfile toGeneralUserProfile;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "from_gup_type_id", nullable = false)
    private GupType FromGupType;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "to_gup_type_id", nullable = false)
    private GupType ToGupType;
    @Column(name = "send_date")
    private Date send_date;
    @Column(name = "is_read")
    private Byte isRead;

}
