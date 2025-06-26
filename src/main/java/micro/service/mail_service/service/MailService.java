package micro.service.mail_service.service;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import micro.service.mail_service.dto.EmailRequestDto;
import micro.service.mail_service.entity.EmailTemplate;
import micro.service.mail_service.entity.SmtpConfig;
import micro.service.mail_service.repository.EmailTemplateRepository;
import micro.service.mail_service.repository.SmtpConfigRepository;

@Service
public class MailService {
	
    private static final Logger LOG = Logger.getLogger(MailService.class.getName());
	
    @Autowired
	private EmailTemplateRepository templateRepo;
    
    @Autowired
	private SmtpConfigRepository smtpConfigRepo;
	
	public String sendMail(EmailRequestDto request) throws MessagingException {
		
		SmtpConfig smtp = smtpConfigRepo.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active SMTP config found"));
		
		EmailTemplate template = templateRepo.findByCodeAndIsActiveTrue(request.getMailCode())
                .orElseThrow(() -> new RuntimeException("Email template not found"));
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(smtp.getHost());
		mailSender.setPort(smtp.getPort());
		mailSender.setUsername(smtp.getUsername());
		mailSender.setPassword(smtp.getPassword());
		
		Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(smtp.isUseTls()));
        props.put("mail.smtp.ssl.enable", String.valueOf(smtp.isUseSsl()));
		
		String subject = processTemplate(template.getSubjectTemplate(),request.getPlaceHolder());
		String body = processTemplate(template.getBodyTemplate(),request.getPlaceHolder());
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, request.getFile()!=null);
		
		helper.setFrom(smtp.getFromEmail());
		helper.setTo(request.getToMail());
		helper.setSubject(subject);
		helper.setText(body, template.isHtml());
		
		if (request.getFile()!=null) {
			helper.addAttachment(request.getFile().getName(), request.getFile());
		}
		
		mailSender.send(mimeMessage);
		
		String message = "Mail Send Successfully";
		
		LOG.info(message);
		
		return message;
	}
	
	private String processTemplate(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }
}
