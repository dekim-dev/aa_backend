//package dekim.aa_backend.controller;
//
//import dekim.aa_backend.dto.CreateAccessTokenRequest;
//import dekim.aa_backend.service.TokenService;
//import lombok.RequiredArgsConstructor;
//import org.apache.coyote.Response;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RequiredArgsConstructor
//@RestController
//public class TokenController {
//  private final TokenService tokenService;
//
//  @PostMapping("/api/token")
//  public ResponseEntity<?> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) {
//    String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
//
//    return ResponseEntity.status(HttpStatus.CREATED)
//            .body(new CreateAccessTokenRequest(createNewAccessToken()));
//  }
//}
