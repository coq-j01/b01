package kr.or.oti.b01.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.or.oti.b01.dto.MemberJoinDTO;
import kr.or.oti.b01.dto.MemberListDTO;

public interface MemberService {
	static class MidExistException extends Exception{
		
	}
	void join(MemberJoinDTO memberJoinDTO) throws MidExistException;
	List<MemberListDTO> memberList();
	Page<MemberListDTO> memberList(String keyword,Pageable pageable);
	void withdraw(String mid);
	MemberListDTO getMember(String mid);
	void modify(MemberListDTO memberDTO);
}
