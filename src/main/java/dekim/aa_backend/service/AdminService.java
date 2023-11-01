package dekim.aa_backend.service;

import dekim.aa_backend.dto.*;
import dekim.aa_backend.entity.*;
import dekim.aa_backend.persistence.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

  private final UserRepository userRepository;
  private final ClinicRepository clinicRepository;
  private final AdvertisementRepository advertisementRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final PostService postService;
  private final UserReportRepository userReportRepository;
  private final InquiryRepository inquiryRepository;

  // 모든 사용자 정보 조회
  @PreAuthorize("hasRole('ADMIN')")
  public Page<UserInfoAllDTO> getAllUserInfo(Pageable pageable) {
    Page<User> userList = userRepository.findAll(pageable);
    // 관리자를 제외한 사용자 정보만 반환
    List<UserInfoAllDTO> userInfoList = userList.stream()
            .filter(user -> !user.getAuthority().name().contains("ADMIN"))
            .map(this::convertToUserInfoAllDTO)
            .collect(Collectors.toList());

    return new PageImpl<>(userInfoList, pageable, userList.getTotalElements());
  }

  private UserInfoAllDTO convertToUserInfoAllDTO(User user) {
    List<String> blockedUserNicknames = user.getBlockedUsers().stream()
            .map(UserBlock::getBlockedUser)
            .map(User::getNickname)
            .collect(Collectors.toList());

    return UserInfoAllDTO.builder()
            .id(user.getId())
            .pfImg(user.getPfImg())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .regDate(user.getRegDate())
            .isPaidMember(user.getIsPaidMember())
            .blockedUsers(user.getBlockedUsers())
            .blockedUserNicknames(blockedUserNicknames)  // blockedUserNicknames 필드 설정
            .build();
  }


  // 회원 삭제
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteMultipleUsers(List<Long> userIds) {
    for (Long userId : userIds) {
      userRepository.deleteById(userId);
    }
  }

  // 회원 정보 수정
  @PreAuthorize("hasRole('ADMIN')")
  public void updateUserInfo(UserInfoAllDTO userInfoAllDTO) {
    Optional<User> optionalUser = userRepository.findById(userInfoAllDTO.getId());
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      if (userInfoAllDTO.getNickname() != null) {
        user.setNickname(userInfoAllDTO.getNickname());
      }
      if (userInfoAllDTO.getEmail() != null) {
        user.setEmail(userInfoAllDTO.getEmail());
      }
      if (userInfoAllDTO.getIsPaidMember() != null) {
        user.setIsPaidMember(userInfoAllDTO.getIsPaidMember());
      }
      userRepository.save(user);
    } else {
      throw new EntityNotFoundException("User not found with ID: " + userInfoAllDTO.getId());
    }
  }

  // 병원 등록
  @PreAuthorize("hasRole('ADMIN')")
  public Clinic registerClinic(ClinicDTO clinicDTO) {
    Clinic clinic = convertToClinic(clinicDTO);
    return clinicRepository.save(clinic);
  }

  // 병원 정보 수정
  @PreAuthorize("hasRole('ADMIN')")
  public Clinic updateClinic(Long clinicId, ClinicDTO clinicDTO) {
    Clinic existingClinic = clinicRepository.findById(clinicId)
            .orElseThrow(() -> new EntityNotFoundException("Clinic not found with ID: " + clinicId));
    existingClinic.setName(clinicDTO.getName());
    existingClinic.setAddress(clinicDTO.getAddress());
    existingClinic.setHpid(clinicDTO.getHpid());
    existingClinic.setDetailedAddr(clinicDTO.getDetailedAddr());
    existingClinic.setTel(clinicDTO.getTel());
    existingClinic.setInfo(clinicDTO.getInfo());
    existingClinic.setLatitude(clinicDTO.getLatitude());
    existingClinic.setLongitude(clinicDTO.getLongitude());
    existingClinic.setViewCount(clinicDTO.getViewCount());
    existingClinic.setScheduleJson(clinicDTO.getScheduleJson());
    return clinicRepository.save(existingClinic);
  }

  // 병원 삭제
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteClinic(List<Long> clinicIds) {
    for (Long clinicId : clinicIds) {
      clinicRepository.deleteById(clinicId);
    }
  }

  private Clinic convertToClinic(ClinicDTO clinicDTO) {
    return Clinic.builder()
            .hpid(clinicDTO.getHpid())
            .name(clinicDTO.getName())
            .address(clinicDTO.getAddress())
            .detailedAddr(clinicDTO.getDetailedAddr())
            .tel(clinicDTO.getTel())
            .info(clinicDTO.getInfo())
            .latitude(clinicDTO.getLatitude())
            .longitude(clinicDTO.getLongitude())
            .scheduleJson(clinicDTO.getScheduleJson())
            .build();
  }

  // 광고 등록
  @PreAuthorize("hasRole('ADMIN')")
  public Advertisement registerAdvertisement(Advertisement advertisement) {
    return advertisementRepository.save(advertisement);
  }

  // 광고 조회 (모든 광고)
  @PreAuthorize("hasRole('ADMIN')")
  public Page<Advertisement> getAdvertisement(Pageable pageable) {
    return advertisementRepository.findAll(pageable);
  }

  // 광고 수정
  @PreAuthorize("hasRole('ADMIN')")
  public Advertisement updateAdvertisement(Long id, AdvertisementDTO advertisementDTO) {

    Advertisement updatedAd = advertisementRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ad Id" + id + "was not found"));

    updatedAd.setAdvertiser(advertisementDTO.getAdvertiser());
    updatedAd.setImgUrl(advertisementDTO.getImgUrl());
    updatedAd.setExpiresOn(advertisementDTO.getExpiresOn());
    return advertisementRepository.save(updatedAd);
  }

  // 광고 삭제
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteAdvertisement(List<Long> ids) {
    for (Long id : ids) {
      advertisementRepository.deleteById(id);
    }
  }

  // 게시글 조회 (모든 게시글)
  @PreAuthorize("hasRole('ADMIN')")
  public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
    Page<Post> postPage = postRepository.findAll(pageable);
    Page<PostResponseDTO> postResponseDTOPage = postPage.map(this::convertToPostResponseDTO);
    return postResponseDTOPage;
  }
  private PostResponseDTO convertToPostResponseDTO(Post post) {
    // 댓글에서 유저의 정보를 사용하기 위해 commentDTO 사용 -> @JsonIgnore..
    List<CommentDTO> commentDTOList;
    if (post.getComments() != null && !post.getComments().isEmpty()) {
      commentDTOList = post.getComments().stream()
              .map(comment -> CommentDTO.builder()
                      .id(comment.getId())
                      .nickname(comment.getUser().getNickname())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .updatedAt(comment.getUpdatedAt())
                      .userId(comment.getUser().getId())
                      .build())
              .toList();
    } else {
      commentDTOList = Collections.emptyList();  // 댓글이 없을경우 emptyList 로 성정
    }
    return PostResponseDTO.builder()
            .id(post.getId())
            .boardCategory(post.getBoardCategory())
            .topic(post.getTopic())
            .title(post.getTitle())
            .content(post.getContent())
            .imgUrl(post.getImgUrl())
            .viewCount(post.getViewCount())
            .likesCount(post.getLikes().size())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .nickname(post.getUser().getNickname())
            .userId(post.getUser().getId())
            .pfImg(post.getUser().getPfImg())
            .commentsDTO(commentDTOList)
            .likes(post.getLikes())
            .build();
  }

  // 게시글 삭제
  @PreAuthorize("hasRole('ADMIN')")
  public void deletePosts(List<Long> ids) {
    for (Long id : ids) {
      postRepository.deleteById(id);
    }
  }

  // 댓글 조회 (모든 댓글)
  @PreAuthorize("hasRole('ADMIN')")
  public Page<CommentDTO> getAllComments(Pageable pageable) {
   Page<Comment> commentPage = commentRepository.findAll(pageable);
   Page<CommentDTO> commentDTOPage = commentPage.map(this::mapToDto);
  return commentDTOPage;
  }
  public CommentDTO mapToDto(Comment comment) {
    CommentDTO dto = new CommentDTO();
    dto.setId(comment.getId());
    dto.setNickname(comment.getUser().getNickname());
    dto.setContent(comment.getContent());
    dto.setCreatedAt(comment.getCreatedAt());
    dto.setUpdatedAt(comment.getUpdatedAt());
    dto.setUserId(comment.getUser().getId());
    dto.setPfImg(comment.getUser().getPfImg());
    if (comment.getPost() != null) {
      dto.setPostId(comment.getPost().getId());
      dto.setPostTitle(comment.getPost().getTitle());
      dto.setPostBoard(comment.getPost().getBoardCategory());
      dto.setPostTopic(comment.getPost().getTopic());
      dto.setLikesCount(comment.getPost().getLikes().size());
    }
    if (comment.getClinic() != null) {
      dto.setClinicId(comment.getClinic().getId());
      dto.setClinicName(comment.getClinic().getName());
    }
    return dto;
  }

  // 댓글 삭제
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteComments(List<Long> ids) {
    for (Long id : ids) {
      commentRepository.deleteById(id);
    }
  }

  // 신고 조회
  @PreAuthorize("hasRole('ADMIN')")
  public Page<ReportResponseDTO> getAllReports(Pageable pageable) {
    Page<UserReport> userReportPage = userReportRepository.findAll(pageable);
    Page<ReportResponseDTO> reportDTOPage = userReportPage
            .map(this::mapUserReportToDTO);  // 엔티티를 DTO로 매핑

    return reportDTOPage;
  }

  // UserReport 엔티티를 ReportResponseDTO로 수동으로 매핑하는 메서드
  public ReportResponseDTO mapUserReportToDTO(UserReport userReport) {
    ReportResponseDTO reportResponseDTO = new ReportResponseDTO();
    reportResponseDTO.setId(userReport.getId());
    reportResponseDTO.setUserId(userReport.getReporter().getId());
    reportResponseDTO.setUserNickname(userReport.getReporter().getNickname());
    reportResponseDTO.setUserEmail(userReport.getReporter().getEmail());
    reportResponseDTO.setReportedUserId(userReport.getReportedUser().getId());
    reportResponseDTO.setReportedUserNickname(userReport.getReportedUser().getNickname());
    reportResponseDTO.setContent(userReport.getContent());
    reportResponseDTO.setReportDate(userReport.getReportDate());
    reportResponseDTO.setManaged(userReport.isManaged());
    return reportResponseDTO;
  }

  // 신고 삭제
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteReports(List<Long> ids) {
    for (Long id : ids) {
      userReportRepository.deleteById(id);
    }
  }

  // 신고 처리
  @PreAuthorize("hasRole('ADMIN')")
  public ReportResponseDTO updateReportStatus(Long reportId) {
    UserReport userReport = userReportRepository.findById(reportId)
            .orElseThrow(() -> new EntityNotFoundException("Report Id" + reportId + "was not found"));
    userReport.setManaged(userReport.isManaged() ? false : true);
    userReportRepository.save(userReport);
    ReportResponseDTO reportResponseDTO = mapUserReportToDTO(userReport);
    return reportResponseDTO;
  }

  // 문의 조회
  @PreAuthorize("hasRole('ADMIN')")
  public Page<InquiryRequestDTO> getAllInquiries(Pageable pageable) {
    Page<Inquiry> inquiryPage = inquiryRepository.findAll(pageable);
    Page<InquiryRequestDTO> reportDTOPage = inquiryPage
            .map(this::mapInquiryToDTO);  // 엔티티를 DTO로 매핑
    return reportDTOPage;
  }

  public InquiryRequestDTO mapInquiryToDTO(Inquiry inquiry) {
    InquiryRequestDTO inquiryRequestDTO = new InquiryRequestDTO();
    inquiryRequestDTO.setId(inquiry.getId());
    inquiryRequestDTO.setUserId(inquiry.getUser().getId());
    inquiryRequestDTO.setUserNickname(inquiry.getUser().getNickname());
    inquiryRequestDTO.setUserEmail(inquiry.getUser().getEmail());
    inquiryRequestDTO.setTitle(inquiry.getTitle());
    inquiryRequestDTO.setContent(inquiry.getContent());
    inquiryRequestDTO.setAnswered(inquiry.isAnswered());
    inquiryRequestDTO.setInquiryDate(inquiry.getInquiryDate());
    return inquiryRequestDTO;
  }

  // 문의 처리
  @PreAuthorize("hasRole('ADMIN')")
  public InquiryRequestDTO updateInquiryStatus(Long inquiryId) {
    Inquiry inquiry = inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new EntityNotFoundException("Inquiry Id" + inquiryId + "was not found"));
    inquiry.setAnswered(inquiry.isAnswered() ? false : true);
    inquiryRepository.save(inquiry);
    InquiryRequestDTO inquiryRequestDTO = mapInquiryToDTO(inquiry);
    return inquiryRequestDTO;
  }
}
