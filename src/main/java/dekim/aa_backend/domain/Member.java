package dekim.aa_backend.domain;

import dekim.aa_backend.constant.IsActive;
import dekim.aa_backend.constant.IsPaidMember;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // 엔티티로 지정 (해당 클래스의 인스턴스들은 JPA로 관리되는 앤티티 객체라는 것을 의미)
@Data // getter, setter ...
@Table(name = "MEMBER_TB") // MEMBER_TB 이름을 가진 테이블과 매핑
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected 기본 생성자 생성. Entity는 반드시 기본생성자가 필요
@AllArgsConstructor // 해당 필드에 쓴 모든 생성자 생성
public class Member {
  @Id // long타입의 id필드를 키본키로 지정
  @GeneratedValue(strategy = GenerationType.AUTO) // 기본키 증가
  @Column(name="memberNo")
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String email;

  @Column(nullable = false, length = 20)
  private String pwd;

  @Column(nullable = false, unique = true, length = 10)
  private String nickname;

  @Column(nullable = false, length = 500)
  private String pfImg;

  @CreationTimestamp // Insert쿼리 발생 시 현재 시간을 자동으로 저장
  @Column(nullable = false)
  private LocalDateTime regDate;

  @Column
  private String authKey;

  @Enumerated(EnumType.STRING)
  private IsPaidMember isPaidMember;

  @Enumerated(EnumType.STRING)
  private IsActive isActive;

  @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Post> posts = new ArrayList<>();

  @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Comment> replies = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private List<TodoList> todoLists = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private List<TodoItem> todoItems = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private List<Diary> diaries = new ArrayList<>();
}

// 📌연관 관계의 주인이 아닌 객체에서 mappedBy 속성을 사용해서 주인을 지정해줘야 하고, 연관관계의 주인은 항상 N