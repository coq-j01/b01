package kr.or.oti.b01.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import kr.or.oti.b01.domain.Board;
import kr.or.oti.b01.domain.Reply;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;
import kr.or.oti.b01.dto.ReplyDTO;
import kr.or.oti.b01.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository repository;
    private final ModelMapper mapper;

    // DTO → Entity
    private Reply dtoToEntity(ReplyDTO replyDTO) {

        Board board = Board.builder()
                .bno(replyDTO.getBno())
                .build();

        return Reply.builder()
                .rno(replyDTO.getRno())
                .board(board)
                .replyText(replyDTO.getReplyText())
                .replyer(replyDTO.getReplyer())
                .build();
    }

    // Entity → DTO
    private ReplyDTO entityToDTO(Reply reply) {

        return ReplyDTO.builder()
                .rno(reply.getRno())
                .bno(reply.getBoard() != null
                        ? reply.getBoard().getBno()
                        : null)
                .replyText(reply.getReplyText())
                .replyer(reply.getReplyer())
                .regDate(reply.getRegDate())
                .modDate(reply.getModDate())
                .build();
    }

    // 댓글 등록
    public ReplyDTO registerReply(ReplyDTO replyDTO) {

        Reply reply = dtoToEntity(replyDTO);

        repository.save(reply);

        return entityToDTO(reply);
    }

    // 댓글 목록 조회
    public PageResponseDTO<ReplyDTO> getReplyList(
            long bno,
            PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("rno").descending()
        );

        Page<Reply> page = repository.listOfBoard(bno, pageable);

        List<ReplyDTO> replyList = page.getContent()
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        int total = (int) page.getTotalElements();

        log.info("replyList : " + replyList);
        log.info("total : " + total);

        return new PageResponseDTO<>(
                pageRequestDTO,
                replyList,
                total
        );
    }

    // 댓글 단건 조회
    public ReplyDTO getReply(long rno) {

        Reply reply = repository.findById(rno)
                .orElseThrow();

        return entityToDTO(reply);
    }

    // 댓글 삭제
    public Long removeReply(long rno) {

        repository.deleteById(rno);

        return rno;
    }

    // 댓글 수정
    public ReplyDTO modifyReply(ReplyDTO replyDTO) {
    	//findbyid로 가져온 후 changetext로 변환하는 방법도 존재!
        Reply reply = dtoToEntity(replyDTO);

        repository.save(reply);

        return entityToDTO(reply);
    }
}