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
import kr.or.oti.b01.dto.BoardDTO;
import kr.or.oti.b01.dto.BoardListAllDTO;
import kr.or.oti.b01.dto.BoardListReplyCountDTO;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.dto.PageResponseDTO;
import kr.or.oti.b01.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
	private final BoardRepository boardRepository;
	private final ModelMapper mapper;

	public void register(BoardDTO boardDTO) {
		//boardRepository.save(mapper.map(boardDTO, Board.class));
		Board board = dtoToEntity(boardDTO);
		boardRepository.save(board);
		System.out.println("DEBUG..." + boardDTO);
	}

	public PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
				Sort.by("bno").descending());
		Page<Board> page = boardRepository.searchAll(pageRequestDTO.getTypes(), pageRequestDTO.getKeyword(), pageable);
		List<BoardDTO> dtoList = page.getContent().stream().map(board -> mapper.map(board, BoardDTO.class))
				.collect(Collectors.toList());

		int total = (int) page.getTotalElements();

		log.info("dtoList : "+ dtoList);
		log.info("total : "+total);
		
		PageResponseDTO<BoardDTO> result = new PageResponseDTO<>(pageRequestDTO, dtoList, total);

		return result;
	} 
	public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
				Sort.by("bno").descending());
		
		Page<BoardListReplyCountDTO> page = boardRepository.searchWithReplyCount(pageRequestDTO.getTypes(), pageRequestDTO.getKeyword(), pageable);
		
		int total = (int) page.getTotalElements();

		PageResponseDTO<BoardListReplyCountDTO> result = new PageResponseDTO<>(pageRequestDTO, page.getContent(), total);

		return result;
	}
	
	public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
				Sort.by("bno").descending());
		
		Page<BoardListAllDTO> page = boardRepository.searchWithAll(pageRequestDTO.getTypes(), pageRequestDTO.getKeyword(), pageable);
		
		int total = (int) page.getTotalElements();

		PageResponseDTO<BoardListAllDTO> result = new PageResponseDTO<>(pageRequestDTO, page.getContent(), total);

		return result;
	}

	public BoardDTO get(long bno) {
		Board board = boardRepository.findByIdWithImages(bno).orElseThrow();
		//return mapper.map(boardRepository.findByIdWithImages(bno), BoardDTO.class);
		return entityToDto(board);
	}

	public void remove(long bno) {
		boardRepository.deleteById(bno);
	}

	public void modify(BoardDTO boardDTO) {
		Board board = boardRepository.findByIdWithImages(boardDTO.getBno()).orElseThrow();

		board.change(boardDTO.getTitle(),boardDTO.getContent());

		// 기존 이미지 삭제
	    board.clearImages();
	    
		if(boardDTO.getFileNames() !=null) {
			boardDTO.getFileNames().forEach(fileName->{
				String[] arr = fileName.split("_",2); //_기준으로 앞은 uuid, 뒤는 파일 명
    			//여기서 파일명에 _가 포함되면 오류가 남 
    			//-> 최대 2개로 나누도록 설정해서 맨처음 _로 구분하도록 함
    			board.addImage(arr[0], arr[1]);
			});
		}
		boardRepository.save(board);
	}
	
	public void removeSelected(List<Long> bnos) {
        if (bnos == null || bnos.isEmpty()) {
            return;
        }
        boardRepository.deleteAllById(bnos);
    }
   
   public void removeAll() {
	   boardRepository.deleteAll();;
   }

}
