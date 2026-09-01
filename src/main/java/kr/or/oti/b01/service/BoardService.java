package kr.or.oti.b01.service;

import java.util.List;
import java.util.stream.Collectors;

import kr.or.oti.b01.domain.Board;
import kr.or.oti.b01.dto.BoardDTO;
import kr.or.oti.b01.dto.BoardListAllDTO;
import kr.or.oti.b01.dto.BoardListReplyCountDTO;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;

public interface BoardService {
	void register(BoardDTO boardDTO);
	PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO);
	BoardDTO get(long bno);
	void remove(long bno);
	void modify(BoardDTO boardDTO);
	void removeSelected(List<Long> bnos);
    void removeAll();
    //댓글
    PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);
    //변환함수
    default Board dtoToEntity(BoardDTO boardDTO) {
    	Board board = Board.builder()
    			.bno(boardDTO.getBno())
    			.title(boardDTO.getTitle())
    			.content(boardDTO.getContent())
    			.writer(boardDTO.getWriter())
    			.build();
    	if(boardDTO.getFileNames() !=null) {
    		boardDTO.getFileNames().forEach(fileName->{
    			String[] arr = fileName.split("_",2); //_기준으로 앞은 uuid, 뒤는 파일 명
    			//여기서 파일명에 _가 포함되면 오류가 남 
    			//-> 최대 2개로 나누도록 설정해서 맨처음 _로 구분하도록 함
    			board.addImage(arr[0], arr[1]);
    		});
    	}
    	return board;
    }
    default BoardDTO entityToDto(Board board) {
    	BoardDTO boardDTO = BoardDTO.builder()
    			.bno(board.getBno())
    			.title(board.getTitle())
    			.content(board.getContent())
    			.writer(board.getWriter())
    			.regDate(board.getRegDate())
    			.modDate(board.getModDate())
    			.build();
    	
    	boardDTO.setFileNames(board.getImageSet().stream().sorted().map(boardImage ->
		boardImage.getUuid()+"_"+boardImage.getFilename())
		.collect(Collectors.toList()));
    	
    	return boardDTO;
    }
}
