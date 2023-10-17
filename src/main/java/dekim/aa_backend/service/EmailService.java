package dekim.aa_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) {
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }


    // 인증키 생성
    private String ePw = createKey();

    // 이메일 인증 링크 보내기
    public void sendEmailWithLink(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("AA <dekim0712@naver.com>");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            message.setContent(content, "text/html; charset=UTF-8");
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    // 비밀번호 재설정 이메일
    public MimeMessage sendEmailWithTempPwd(String email) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("[Appropriate Attention] 비밀번호 재발급");
        String msg="";
        msg += "<p>안녕하세요.</p>";
        msg += "<p>Appropriate Attention 계정의 임시 비밀번호가 발급되었습니다.</p>";
        msg += "<p>로그인 시 아래 비밀번호를 입력해 주세요.</p>";
        msg += "<h3>임시 비밀번호 : " + ePw + "</h3>";
        msg += "<p>보안을 위해 로그인 후 비밀번호를 꼭 변경해 주세요.</p>";
        msg += "<p>감사합니다.</p>";
        message.setText(msg, "utf-8", "html"); // 내용
        message.setFrom(new InternetAddress("dekim0712@naver.com", "AA")); // 보내는 사람
        return message;
    }

    // 이메일 보내기
    public String sendPasswordAuthKey(String to) throws Exception {
        MimeMessage message = sendEmailWithTempPwd(to); // 메일 발송
        try {// 예외처리
            javaMailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw; // 메일로 보냈던 비밀번호(인증코드)를 반환
    }
}
