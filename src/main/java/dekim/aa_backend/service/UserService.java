package dekim.aa_backend.service;

import dekim.aa_backend.dto.CommentDTO;
import dekim.aa_backend.dto.PostResponseDTO;
import dekim.aa_backend.dto.ReportRequestDTO;
import dekim.aa_backend.dto.UserInfoAllDTO;
import dekim.aa_backend.entity.*;
import dekim.aa_backend.exception.DuplicatePostReportException;
import dekim.aa_backend.persistence.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  @Autowired
  UserRepository userRepository;
  @Autowired
  PostRepository postRepository;
  @Autowired
  CommentRepository commentRepository;
  @Autowired
  UserBlockRepository userBlockRepository;
  @Autowired
  UserReportRepository userReportRepository;
  @Autowired
  PostReportRepository postReportRepository;

  // 내 글 보기
  public Page<PostResponseDTO> getUserPost(Long userId, int page, int pageSize) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    User user = userOptional.get();
    PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Post> postPage = postRepository.findByUserId(user.getId(), pageRequest);
    return postPage.map(post -> {
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
              .build();
    });
  }

  // 내 글 삭제
  public void deleteMultiplePosts( Long userId, List<Long> postIds) {
    for (Long postId : postIds) {
      Optional<Post> postOptional = postRepository.findById(postId);
      if (postOptional.isPresent()) {
        Post post = postOptional.get();
        // Check if the logged-in user is the author of the post
        if (!post.getUser().getId().equals(userId)) {
          throw new RuntimeException("You are not authorized to delete post with id: " + postId);
        }
        postRepository.deleteById(postId);
      }
    }
  }

  // 내 댓글 보기
  public Page<CommentDTO> getUserComment(Long userId, int page, int pageSize) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    User user = userOptional.get();
    PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    Page<Comment> commentPage = commentRepository.findByUserId(user.getId(), pageRequest);
    return commentPage.map(comment -> {
      CommentDTO.CommentDTOBuilder commentDTOBuilder = CommentDTO.builder()
              .id(comment.getId())
              .content(comment.getContent())
              .createdAt(comment.getCreatedAt())
              .updatedAt(comment.getUpdatedAt())
              .nickname(comment.getUser().getNickname())
              .userId(comment.getUser().getId());

      if (comment.getPost() != null) {
        commentDTOBuilder
                .postId(comment.getPost().getId())
                .postTitle(comment.getPost().getTitle())
                .postBoard(comment.getPost().getBoardCategory())
                .postTopic(comment.getPost().getTopic())
                .likesCount(comment.getPost().getLikesCount());
      }

      if (comment.getClinic() != null) {
        commentDTOBuilder.clinicId(comment.getClinic().getId()).clinicName(comment.getClinic().getName());
      }
      return commentDTOBuilder.build();
    });
  }

  // 내 댓글 삭제
  public void deleteMultipleComments(Long userId, List<Long> commentIds) {
    for (Long commentId : commentIds) {
      Optional<Comment> commentOptional = commentRepository.findById(commentId);
      if (commentOptional.isPresent()) {
        Comment comment = commentOptional.get();
        // Check if the logged-in user is the author of the post
        if (!comment.getUser().getId().equals(userId)) {
          throw new RuntimeException("You are not authorized to delete comment with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
      }
    }
  }

  // 회원정보 가져오기
  public UserInfoAllDTO getUserInfo(Long userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    User user = userOptional.get();
    return UserInfoAllDTO.builder()
            .id(user.getId())
            .pfImg(user.getPfImg())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .regDate(user.getRegDate())
            .isPaidMember(user.getIsPaidMember())
            .postCount(user.getPosts().size())
            .commentCount(user.getComments().size())
            .likes(user.getLikes())
            .build();
  }

  // 회원정보 수정 (닉네임)
  public UserInfoAllDTO updateUserNickname(Long userId, String newNickname) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    User user = userOptional.get();
    user.setNickname(newNickname);
    userRepository.save(user);
    return getUserInfo(userId);
  }

  // 회원정보 수정 (프로필 사진)
  public UserInfoAllDTO updateUserPfImg(Long userId, String newPfImg) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    User user = userOptional.get();
    user.setPfImg(newPfImg);
    userRepository.save(user);
    return getUserInfo(userId);
  }

  // 회원 탈퇴
  public void deleteUser(Long userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    userRepository.deleteById(userId);
  }

  // 회원 차단
  public void blockUser(Long userId, Long blockedUserId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    User blockedUser = userRepository.findById(blockedUserId)
            .orElseThrow(() -> new IllegalArgumentException("User not found :" + blockedUserId));

    if(user.equals(blockedUser)) {
      throw new IllegalArgumentException("You cannot block yourself.");
    }

    boolean isBlockedAlready = userBlockRepository.findByBlockerAndBlockedUser(user, blockedUser).isPresent();
    if(isBlockedAlready) {
      throw new IllegalArgumentException("You've already blocked this user.");
    }

    UserBlock userBlock = new UserBlock();
    userBlock.setBlocker(user);
    userBlock.setBlockedUser(blockedUser);
    userBlockRepository.save(userBlock);
  }

  // 회원 차단 해제
  public void unblockUser(Long userId, Long blockedUserId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    User blockedUser = userRepository.findById(blockedUserId)
            .orElseThrow(() -> new IllegalArgumentException("User not found :" + blockedUserId));

    UserBlock userBlock = userBlockRepository.findByBlockerAndBlockedUser(user, blockedUser)
            .orElseThrow(() -> new IllegalArgumentException("You've not blocked this user."));
    userBlockRepository.delete(userBlock);
  }

  // 회원 신고
  public void reportUser(Long userId, ReportRequestDTO reportRequestDTO) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    User reportedUser = userRepository.findById(reportRequestDTO.getReportedUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found :" + reportRequestDTO.getReportedUserId()));

    if(user.equals(reportedUser)) {
      throw new IllegalArgumentException("You cannot block yourself.");
    }

    UserReport userBlock = new UserReport();
    userBlock.setReporter(user);
    userBlock.setReportedUser(reportedUser);
    userBlock.setContent(reportRequestDTO.getContent());
    userBlock.setReportDate(reportRequestDTO.getReportDate());
    userReportRepository.save(userBlock);
  }

  // 회원 신고
  public void cancelReportUser(Long userId, Long reportId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    UserReport userReport = userReportRepository.findById(reportId)
            .orElseThrow(() -> new EntityNotFoundException("Report not found"));

    if(user.equals(userReport.getReporter())) {
      userReportRepository.delete(userReport);
    }
  }

  // 게시글 신고
  public void reportPost(Long userId, Long postId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

    Optional<PostReport> reportedPostOptional = postReportRepository.findByUserAndPost(user, post);
    if (reportedPostOptional.isPresent()) {
      post.setReportCount(post.getReportCount() - 1);

      PostReport reportedPost = reportedPostOptional.get();
      postReportRepository.delete(reportedPost);
      throw new DuplicatePostReportException("게시글 신고를 취소하였습니다.");
    }

    if (user.getId().equals(post.getUser().getId())) {
      throw new IllegalArgumentException("You cannot report your own post.");
    } else {
      post.setReportCount(post.getReportCount() + 1);
      postRepository.save(post);

      // 게시글 신고 정보 저장
      PostReport postReport = new PostReport();
      postReport.setUser(user);
      postReport.setPost(post);
      postReportRepository.save(postReport);

      if (post.getReportCount() >= 3) {
        // 게시글이 3회 이상 누적시 게시글 삭제 처리
        postRepository.delete(post);
      }
    }
  }

}
