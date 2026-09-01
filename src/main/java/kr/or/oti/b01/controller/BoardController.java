package kr.or.oti.b01.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.oti.b01.dto.BoardDTO;
import kr.or.oti.b01.dto.PageRequestDTO;
import kr.or.oti.b01.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
	private final BoardService boardservice;

	@RequestMapping("/list")
	public void list(PageRequestDTO pageRequestDTO, Model model) {
		model.addAttribute("pageResponseDTO", boardservice.listWithAll(pageRequestDTO));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/read")
	public void read(long bno, PageRequestDTO pageRequestDTO, Model model) {
		// JSP를 출력할 수 있게 dto 설정함
		model.addAttribute("dto", boardservice.get(bno));
	}

	@GetMapping("/modify")
	public String modify(long bno, Principal principal, PageRequestDTO pageRequestDTO, Model model, RedirectAttributes redirectAttributes) {
		BoardDTO boardDTO = boardservice.get(bno);
		if(principal.getName().equals(boardDTO.getWriter())) {
			// JSP를 출력할 수 있게 dto 설정함
			model.addAttribute("dto",boardDTO);
			return "board/modify";
		}else {
			redirectAttributes.addFlashAttribute("error","로그인한 사용자와 게시물 작성자가 달라 수정이 불가합니다.");
			return "redirect:read?bno="+ bno+"&" + pageRequestDTO.getLink();
		}
	}

	@PreAuthorize("principal.username == #dto.writer")
	@PostMapping("/modify")
	public String modify(PageRequestDTO pageRequestDTO, @Valid BoardDTO dto, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			log.info("errors" + bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/board/modify?" + pageRequestDTO.getLink();
		}

		boardservice.modify(dto);

		// 목록으로 이동한다
		return "redirect:list?" + pageRequestDTO.getLink();
	}

	@PreAuthorize("principal.username == #dto.writer")
	@PostMapping("/remove")
	public String remove(long bno, BoardDTO dto, PageRequestDTO pageRequestDTO) {
		boardservice.remove(bno);

		// 목록으로 이동한다
		return "redirect:list?" + pageRequestDTO.getLink();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/removeSelected")
	public String removeSelected(@RequestParam("bno") List<Long> bnos, PageRequestDTO pageRequestDTO) {

		boardservice.removeSelected(bnos);

		return "redirect:list?" + pageRequestDTO.getLink();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/removeAll")
	public String removeAll() {
		boardservice.removeAll();

		// 삭제 후 list페이지로 리다이렉트
		return "redirect:list";
	}

	@PreAuthorize("hasRole('USER')") // USER권한이 있는지 없는지 확인
	@GetMapping("/register")
	public void register() {
	}

	@PostMapping("/register")
	public String register(@Valid BoardDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			log.info("errors" + bindingResult.getAllErrors());
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/board/register";
		}
		log.info("fileNames = {}", dto.getFileNames());

		boardservice.register(dto);
		redirectAttributes.addFlashAttribute("result", dto.getBno());

		// 할일 등록 목록으로 이동한다
		return "redirect:/board/list";
	}

}
