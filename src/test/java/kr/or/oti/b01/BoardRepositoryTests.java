package kr.or.oti.b01;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import kr.or.oti.b01.domain.Board;
import kr.or.oti.b01.dto.BoardListAllDTO;
import kr.or.oti.b01.repository.BoardRepository;
import kr.or.oti.b01.repository.ReplyRepository;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class BoardRepositoryTests {
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private ReplyRepository replyRepository;
	
//	@Test
//	public void insertTest() {
//		Board board = Board.builder()
//				.title("title ...0")
//				.content("content ...0")
//				.writer("writer0")
//				.build();
//		Board result = boardRepository.save(board);
//		log.info("BNO : "+result.getBno());
//	}
//	
//	@Test
//	public void selectTest() {
//		Long bno = 1L;
//		Optional<Board> result = boardRepository.findById(bno);
//		Board board = result.orElseThrow();
//		log.info("BNO : "+board);
//	}
//	
//	@Test
//	public void updateTest() {
//		Long bno = 1L;
//		Optional<Board> result = boardRepository.findById(bno);
//		Board board = result.orElseThrow();
//		board.change("update..title 1","update..content 1");
//		Board result2 = boardRepository.save(board);
//		log.info("수정 : "+result2);
//	}
//	
//	@Test
//	public void deleteTest() {
//		Long bno = 2L;
//		boardRepository.deleteById(bno);
//	}
//	
//	@Test
//	public void testPaging() {
//		PageRequest pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
//		Page<Board> result = boardRepository.findAll(pageable);
//		
//		log.info("result : "+result);
//	}
//	
//	@Test
//    public void testPaging2() {
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
//        Page<Board> result = boardRepository.findkeyword("9", pageable);
//        
//        log.info("total count: "+result.getTotalElements());
//        log.info( "total pages:" +result.getTotalPages());
//        log.info("page number: "+result.getNumber());
//        log.info("page size: "+result.getSize());
//
//        List<Board> todoList = result.getContent();
//
//        todoList.forEach(board -> log.info(board.toString()));
//    }
//	
//	@Test
//    public void testPaging3() {
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
//        Page<Board> result = boardRepository.search1(pageable);
//        
//        log.info("total count: "+result.getTotalElements());
//        log.info( "total pages:" +result.getTotalPages());
//        log.info("page number: "+result.getNumber());
//        log.info("page size: "+result.getSize());
//
//        List<Board> todoList = result.getContent();
//
//        todoList.forEach(board -> log.info(board.toString()));
//    }
//	@Test
//    public void testPaging4() {
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
//        Page<Board> result = boardRepository.searchAll(new String[] {"w"},"0",pageable);
//        
//        log.info("total count: "+result.getTotalElements());
//        log.info( "total pages:" +result.getTotalPages());
//        log.info("page number: "+result.getNumber());
//        log.info("page size: "+result.getSize());
//
//        List<Board> todoList = result.getContent();
//
//        todoList.forEach(board -> log.info(board.toString()));
//    }
//	@Test
//	public void 이미지_첨부파일_테스트() {
//		Board board = Board.builder()
//				.title("image Test")
//				.content("첨부파일 테스트")
//				.writer("홍길동")
//				.build();
//		for(int i=0; i<3; i++) {
//			board.addImage(UUID.randomUUID().toString(), "고양이"+i+".jpg");
//		}
//		boardRepository.save(board);
//	}
//	
//	@Test
//	public void 이미지_첨부파일_읽기_테스트() {
//		Long bno = 27L;
//		Optional<Board> result = boardRepository.findById(bno);
//		
//		Board board = result.orElseThrow();
//		log.info("img"+board.getImageSet());
//	}
//	@Test
//	public void 이미지_첨부파일_읽기_테스트2() {
//		Long bno = 27L;
//		Optional<Board> result = boardRepository.findByIdWithImages(bno);
//		
//		Board board = result.orElseThrow();
//		log.info("img"+board.getImageSet());
//	}
//	
//	@Test
//    @Transactional
//    public void 게시물첨부파일읽기_테스트2() {
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
//
//        Page<Board> result = boardRepository.findAll(pageable);
//
//        log.info("total count: " + result.getTotalElements());
//        log.info("total pages: " + result.getTotalPages());
//        log.info("page number: " + result.getNumber());
//        log.info("page size: " + result.getSize());
//
//        List<Board> todoList = result.getContent();
//
//        todoList.forEach(board -> {
//            log.info("board: " + board);
//            log.info("imageSet" + board.getImageSet());
//        });
//    }
//	
//	@Transactional
//	@Commit
//	@Test
//	public void 게시물_수정_첨부파일_삭제_후_첨부파일_추가_테스트() {
//		Long bno = 27L;
//		Optional<Board> result = boardRepository.findByIdWithImages(bno);
//		
//		Board board = result.orElseThrow();
//		
//		//기존 이미지 삭제
//		board.clearImages();
//		//새로운 첨부파일들
//		for(int i=0; i<2; i++) {
//			board.addImage(UUID.randomUUID().toString(), "수정된_고양이"+i+".jpg");
//		}
//		
//		boardRepository.save(board);
//	}
//	
//	@Transactional
//	@Commit
//	@Test
//	public void 게시물과_댓글_첨부파일_삭제_테스트() {
//		Long bno = 27L;
//		replyRepository.deleteByBoard_Bno(bno);
//		boardRepository.deleteById(bno);
//	}
//	
//	@Test
//	public void  게시물등록시_아이디가_5의_배수가_아니면_첨부파일_추가함_테스트() {
//		IntStream.range(1, 100).forEach(i -> {
//			Board board = Board.builder().title("title..." + i).content("content..." + i).writer("user" + (i % 10))
//					.build();
//
//			for (int j=0;j<3;j++) {
//				if (i % 5 == 0) continue;
//				board.addImage(UUID.randomUUID().toString(), i + "_고양이_" + j + ".jpg");
//			}
//			Board result = boardRepository.save(board);
//			log.info("BNO: " + result.getBno());
//		});
//	}
//	
//	@Test
//    @Transactional
//    public void 게시물_첨부파일읽기_테스트3() {
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
//
//        Page<BoardListAllDTO> result = boardRepository.searchWithAll(null, null, pageable);
//
//        log.info("total count: " + result.getTotalElements());
//        log.info("total pages: " + result.getTotalPages());
//        log.info("page number: " + result.getNumber());
//        log.info("page size: " + result.getSize());
//
//        List<BoardListAllDTO> todoList = result.getContent();
//
//        todoList.forEach(board -> {
//            log.info("board: " + board);
//            log.info("imageSet" + board.getBoardImages());
//        });
//    }
}
