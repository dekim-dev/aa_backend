package dekim.aa_backend.service;

import dekim.aa_backend.dto.CommentDTO;
import dekim.aa_backend.dto.PostResponseDTO;
import dekim.aa_backend.dto.UserInfoAllDTO;
import dekim.aa_backend.entity.Comment;
import dekim.aa_backend.entity.Post;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.CommentRepository;
import dekim.aa_backend.persistence.PostRepository;
import dekim.aa_backend.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
              // Map other fields as needed
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
}
