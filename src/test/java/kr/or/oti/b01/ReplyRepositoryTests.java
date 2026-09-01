package kr.or.oti.b01;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

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
import kr.or.oti.b01.domain.Reply;
import kr.or.oti.b01.dto.BoardListReplyCountDTO;
import kr.or.oti.b01.repository.BoardRepository;
import kr.or.oti.b01.repository.ReplyRepository;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class ReplyRepositoryTests {
	@Autowired
	private ReplyRepository repository;
	
	@Autowired
	private BoardRepository boardRepository;

	@Test
	public void insertTest() {
		Board board = Board.builder().bno(22L).build();

		IntStream.range(1, 100).forEach(i -> {
			Reply reply = Reply.builder().board(board).replyText("댓글..." + i).replyer("홍길동").build();
			Reply result = repository.save(reply);
			log.info("RNO : " + result.getRno());
		});
	}

	@Test
	public void testBoardReplies() {
		Long bno = 23L;

		Pageable pageable = PageRequest.of(0, 10, Sort.by("rno").descending());

		Page<Reply> result = repository.listOfBoard(bno, pageable);
		result.getContent().forEach(reply -> {
			log.info("reply" + reply);
		});
		log.info("total = {} ", result.getTotalElements());
	}

	@Test
    public void testSearchReplyCountPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(new String[] {"t"}, "말랑", pageable);

        log.info("total count: " + result.getTotalElements());
        log.info("total pages:" + result.getTotalPages());
        log.info("page number: " + result.getNumber());
        log.info("page size: " + result.getSize());

        List<BoardListReplyCountDTO> todoList = result.getContent();

        todoList.forEach(board -> log.info(board.toString()));
    }
}
