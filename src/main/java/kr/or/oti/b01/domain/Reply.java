package kr.or.oti.b01.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
@Table(name="Reply", indexes= {@Index(name="idx_reply_board_bno",columnList = "board_bno")})
public class Reply extends BaseEntity{ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @Column(length = 2000, nullable = false)
    private String replyText;

    @Column(length = 50, nullable = false)
    private String replyer;


    //댓글이 변동이 더 많으니 연관관계를 댓글에 지정
    @ManyToOne(fetch = FetchType.LAZY)//Lazy : 천천히 동작해라 -> 빨리 가져오면 혼란스러워 순서를 음,,, 그렇대여!
    private Board board;
}