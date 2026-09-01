package kr.or.oti.b01.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Member extends BaseEntity{

   @Id
   private String mid;
   
   private String mpw;
   
   private String email;
   
   private boolean del;
   
   private boolean social;
   
   private int failCount;
   @Builder.Default
   private boolean accountNonLocked = true; // 로그인 실패 횟수 5회가 되면 false
   private LocalDate expiredDate;
   private LocalDate passwordChangedDate;
   
   @ElementCollection(fetch = FetchType.LAZY)
   @Builder.Default
   private Set<MemberRole> roleSet = new HashSet<>();
   
   public void changePassword(String mpw) {
      this.mpw = mpw;
      this.passwordChangedDate = LocalDate.now();
   }
   
   public void changeEmail(String email) {
      this.email = email;
   }
   
   public void changeDel(boolean del) {
      this.del = del;
   }
   
   public void addRole(MemberRole memberRole) {
      this.roleSet.add(memberRole);
   }
   
   public void clearRoles() {
      this.roleSet.clear();
   }
   
   //로그인 작업
   public void changeFailCount(int failCount) {
		this.failCount = failCount;
	}

	public void changeAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}
	//만료일 변경 설정
	public void changeExpiredDate(LocalDate expiredDate) {
		this.expiredDate = expiredDate;
	}
   
}