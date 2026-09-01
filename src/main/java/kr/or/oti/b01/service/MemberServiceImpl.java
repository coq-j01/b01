package kr.or.oti.b01.service;



import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.or.oti.b01.domain.Member;
import kr.or.oti.b01.domain.MemberRole;
import kr.or.oti.b01.dto.MemberJoinDTO;
import kr.or.oti.b01.dto.MemberListDTO;
import kr.or.oti.b01.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
	private final ModelMapper modelMapper;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
		String mid = memberJoinDTO.getMid();
		boolean exist = memberRepository.existsById(mid);
		
		if(exist) {
			throw new MidExistException();
		}
		Member member = modelMapper.map(memberJoinDTO, Member.class);
		member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw()));
		member.addRole(MemberRole.USER);
		
		log.info("member : "+member);
		log.info("roleSet : "+member.getRoleSet());
		
		memberRepository.save(member);
	}

	@Override
	public List<MemberListDTO> memberList() {
		List<Member> memberList = memberRepository.findAll();
		
		return memberList.stream()
	            .map(member -> modelMapper.map(member, MemberListDTO.class))
	            .collect(Collectors.toList());
	}
	@Override
	public Page<MemberListDTO> memberList(String keyword,Pageable pageable) {

	    Page<Member> result;

	    if (keyword == null || keyword.trim().isEmpty()) {

	        result = memberRepository.findAll(pageable);

	    } else {

	        result = memberRepository
	                .findByMidContainingOrEmailContaining(
	                        keyword,
	                        keyword,
	                        pageable
	                );
	    }

	    return result.map(member ->
	            modelMapper.map(member, MemberListDTO.class)
	    );
	}
	@Override
	public void withdraw(String mid) {

	    Member member = memberRepository
	            .findById(mid)
	            .orElseThrow();

	    member.changeDel(true);

	    memberRepository.save(member);
	}
	@Override
	public MemberListDTO getMember(String mid) {

	    Member member = memberRepository
	            .findById(mid)
	            .orElseThrow();

	    return modelMapper.map(member, MemberListDTO.class);
	}
	@Override
	public void modify(MemberListDTO memberDTO) {

	    Member member = memberRepository
	            .findById(memberDTO.getMid())
	            .orElseThrow();

	    member.changeEmail(memberDTO.getEmail());

	    // 필요하다면 권한도 수정
	    member.clearRoles();

	    memberDTO.getRoleSet().forEach(role -> {
	        member.addRole(role);
	    });

	    memberRepository.save(member);
	}
}
