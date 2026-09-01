package kr.or.oti.b01.domain;

import java.util.Set;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;

import kr.or.oti.b01.dto.BoardListAllDTO;
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
@ToString
public class Board extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(length = 500, nullable = false) //컬럼의 길이와 null허용여부
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;
    
    @OneToMany(mappedBy = "board",  //BoardImage의 board 변수
    		cascade = {CascadeType.ALL},
    		fetch = FetchType.LAZY,
    		orphanRemoval = true)
    @Builder.Default
    @BatchSize(size=20)
    private Set<BoardImage> imageSet = new HashSet<>();
    
    public void change(String title, String content) {
    	this.title = title;
    	this.content = content;
    }
    //이미지 추가
    public void addImage(String uuid, String filename) {
    	BoardImage boardImage = BoardImage.builder()
    			.uuid(uuid)
    			.filename(filename)
    			.board(this)
    			.ord(imageSet.size())
    			.build();
    	imageSet.add(boardImage);
    }
    //이미지 제거 -> null로 설정
    public void clearImages() {
    	//Board 제거
    	//for문
//    	for(BoardImage image : imageSet) {
//    		image.changeBoard(null);
//    	}
    	//스트림으로 작성
    	imageSet.forEach(image -> image.changeBoard(null));
    	//이때 board가 연결되어있어서 이 연결된 board를 제거해야함
    	imageSet.clear();
    }
    public BoardListAllDTO of(Long replyCount) {
    	return BoardListAllDTO.builder()
    			.bno(bno)
    			.title(title)
    			.writer(writer)
    			.regDate(getRegDate())
    			.replyCount(replyCount)
    			.build();
    }
    
    @OneToMany
    private Set<Reply> replies; //양방향
}