package dekim.aa_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GlobalResponseDTO {
  private String msg;
  private int statusCode;

  private GlobalResponseDTO(String msg, int statusCode) {
    this.msg = msg;
    this.statusCode = statusCode;
  }

  public static GlobalResponseDTO of(String msg, int statusCode) {
    return new GlobalResponseDTO(msg, statusCode);
  }
}
