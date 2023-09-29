package dekim.aa_backend.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
@Getter
@ToString
public class AdvertisementDTO {
    private String advertiser;
    private String imgUrl;
    private LocalDate expiresOn;
}
