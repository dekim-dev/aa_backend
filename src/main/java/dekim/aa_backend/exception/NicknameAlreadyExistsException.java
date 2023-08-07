package dekim.aa_backend.exception;

public class NicknameAlreadyExistsException extends RuntimeException {
  public NicknameAlreadyExistsException(String nickname) {
    super("Nickname already exists: " + nickname);
  }
}