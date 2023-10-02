package dekim.aa_backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
@Getter
@Setter
@ToString
public class AdvertisementDTO {
    private Long id;
    private String advertiser;
    private String imgUrl;
    private LocalDate expiresOn;
}
