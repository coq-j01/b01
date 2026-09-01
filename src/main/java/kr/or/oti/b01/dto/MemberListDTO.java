package kr.or.oti.b01.dto;

import java.util.Set;

import groovy.transform.builder.Builder;
import kr.or.oti.b01.domain.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberListDTO {

    private String mid;
    private String email;
    private boolean del;
    private Set<MemberRole> roleSet;
}