package dekim.aa_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dekim.aa_backend.entity.Clinic;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.ClinicRepository;
import dekim.aa_backend.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicService {

  @Value("${public-data-service-key}")
  private String serviceKey;

  private final ClinicRepository clinicRepository;
  private final UserRepository userRepository;


  /* 공공 데이터 가져오기 */
  private JsonNode fetchClinicDataFromPublicApi() throws IOException {
    // 요청을 위한 URL 생성
    String urlBuilder = "http://apis.data.go.kr/B552657/HsptlAsembySearchService/getHsptlMdcncListInfoInqire" + "?" + "serviceKey=" + serviceKey +
            "&" + URLEncoder.encode("QD", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("D004", StandardCharsets.UTF_8) +
            "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("100", StandardCharsets.UTF_8);
    URL url = new URL(urlBuilder);

    // URL 연결헤서 데이터 가져오기
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    log.info("📌Response code: " + conn.getResponseCode());

    BufferedReader rd;
    if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
      rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    } else {
      rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
    }

    // 가져온 데이터를 문자열로 변환하여 저장
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      sb.append(line);
    }
    rd.close();
    conn.disconnect();

    // XML -> JSON 변환
    ObjectMapper xmlMapper = new XmlMapper();
    JsonNode jsonNode = xmlMapper.readTree(sb.toString());
    return jsonNode.get("body").get("items").get("item");
  }


  /* 가져온 데이터 Clinic entity로 변환 */
  private Clinic createClinicFromJson(JsonNode clinicJson) throws JsonProcessingException {
    Clinic clinic = Clinic.builder()
            .hpid(clinicJson.get("hpid").asText())
            .name(clinicJson.get("dutyName").asText())
            .address(clinicJson.get("dutyAddr").asText())
            .detailedAddr(clinicJson.has("dutyMapimg") && !clinicJson.get("dutyMapimg").isNull()
                    ? clinicJson.get("dutyMapimg").asText()
                    : "-")
            .info(clinicJson.has("dutyInf") && !clinicJson.get("dutyInf").isNull()
                    ? clinicJson.get("dutyInf").asText()
                    : "-")
            .tel(clinicJson.get("dutyTel1").asText())
            .latitude(clinicJson.get("wgs84Lat").asDouble())
            .longitude(clinicJson.get("wgs84Lon").asDouble())
            .recommendation(0)
            .build();


    // 스케줄 정보 가져오기
    List<Map<String, String>> scheduleList = new ArrayList<>();
    for (int i = 1; i <= 8; i++) {
      JsonNode dutyTimeNodeC = clinicJson.get("dutyTime" + i + "c");
      JsonNode dutyTimeNodeS = clinicJson.get("dutyTime" + i + "s");

      if (dutyTimeNodeC != null && dutyTimeNodeS != null) {
        String timeC = dutyTimeNodeC.asText();
        String timeS = dutyTimeNodeS.asText();

        if (!timeC.equals("0000") && !timeS.equals("0000")) {
          Map<String, String> schedule = new HashMap<>();
          schedule.put("dayOfWeek", String.valueOf(i));
          schedule.put("startTime", timeS);
          schedule.put("endTime", timeC);
          scheduleList.add(schedule);
        }
      }
    }
    // Json format으로 변환
    ObjectMapper objectMapper = new ObjectMapper();
    String scheduleJson = objectMapper.writeValueAsString(scheduleList);
    clinic.setScheduleJson(scheduleJson);

    return clinic;
  }


  /* DB에 가져온 데이터 저장 */
  public void insertClinicDataToDB() throws JsonProcessingException {
    JsonNode clinicData;
    try {
      clinicData = fetchClinicDataFromPublicApi();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    List<Clinic> clinicList = new ArrayList<>();
    for (JsonNode clinicJson : clinicData) {
      Clinic clinic = createClinicFromJson(clinicJson);
      clinicList.add(clinic);
    }
    clinicRepository.saveAll(clinicList);
  }


  /* Clinic data 업데이트 */
  public void updateClinicsFromPublicData() throws IOException {
    JsonNode clinicData;
    try {
      clinicData = fetchClinicDataFromPublicApi();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    for (JsonNode clinicJson : clinicData) {
      String hpid = clinicJson.get("hpid").asText();
      if (!clinicRepository.existsByHpid(hpid)) {
        Clinic clinic = createClinicFromJson(clinicJson);
        clinicRepository.save(clinic);
        log.info("📌저장된 병원: " + clinic.getName());
      }
    }
  }


  /* DB에서 병원 데이터 가져오기 */
  public Page<Clinic> fetchClinicList(int page, int pageSize) {
    PageRequest pageRequest = PageRequest.of(page, pageSize);
    return clinicRepository.findAll(pageRequest);
  }


  /* 키워드로 병원 검색 */
  public Page<Clinic> searchClinicsByKeyword(String keyword, Pageable pageable) {
    return clinicRepository.findByNameContaining(keyword, pageable);
  }


  /* id로 병원 정보 가져오기 */
  public Optional<Clinic> getClinicInfoById(Long id) {
    return clinicRepository.findById(id);
  }


  /* 키워드로 병원 검색 */
  public Page<Clinic> searchClinicsByAddress(String address, Pageable pageable) {
    return clinicRepository.findByAddressContaining(address, pageable);
  }

}