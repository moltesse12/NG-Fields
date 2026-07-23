package tg.ngstars.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import tg.ngstars.auth.config.ResendProperties;

@Service
@EnableConfigurationProperties(ResendProperties.class)
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final Resend resend;
    private final ResendProperties properties;

    public EmailService(ResendProperties properties) {
        this.properties = properties;
        this.resend = new Resend(properties.apiKey());
    }

    public void sendCredentialsEmail(String toEmail, String firstName, String tempPassword) {
        var html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                  <h2 style="color: #40946e;">Bienvenue sur NG-STARs</h2>
                  <p>Bonjour %s,</p>
                  <p>Votre compte a ete cree. Voici vos identifiants de connexion :</p>
                  <div style="background: #f4f4f4; padding: 16px; border-radius: 8px; margin: 16px 0;">
                    <p><strong>Email :</strong> %s</p>
                    <p><strong>Mot de passe temporaire :</strong> <code style="background: #e8e8e8; padding: 2px 6px; border-radius: 4px;">%s</code></p>
                  </div>
                  <p style="color: #c00; font-weight: bold;">
                    Important : Vous devrez changer votre mot de passe lors de votre premiere connexion.
                  </p>
                  <p>
                    <a href="%s" style="display: inline-block; background: #40946e; color: white; padding: 10px 24px; border-radius: 6px; text-decoration: none; font-weight: bold;">
                      Se connecter
                    </a>
                  </p>
                  <hr style="border: none; border-top: 1px solid #ddd; margin: 24px 0;" />
                  <p style="font-size: 12px; color: #888;">
                    Ceci est un email automatique. Ne repondez pas a cet email.
                  </p>
                </div>
                """.formatted(firstName, toEmail, tempPassword, getLoginUrl());

        send(toEmail, "Bienvenue sur NG-STARs - Vos identifiants de connexion", html);
    }

    public void sendPasswordResetEmail(String toEmail, String firstName, String resetLink) {
        var html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                  <h2 style="color: #40946e;">Reinitialisation du mot de passe</h2>
                  <p>Bonjour %s,</p>
                  <p>Vous avez demande la reinitialisation de votre mot de passe.</p>
                  <p>
                    <a href="%s" style="display: inline-block; background: #40946e; color: white; padding: 10px 24px; border-radius: 6px; text-decoration: none; font-weight: bold;">
                      Reinitialiser mon mot de passe
                    </a>
                  </p>
                  <p style="font-size: 12px; color: #888;">
                    Si vous n'avez pas demande cette reinitialisation, ignorez cet email.
                  </p>
                </div>
                """.formatted(firstName, resetLink);

        send(toEmail, "NG-STARs - Reinitialisation du mot de passe", html);
    }

    private void send(String to, String subject, String html) {
        try {
            var params = CreateEmailOptions.builder()
                    .from(properties.fromName() + " <" + properties.fromEmail() + ">")
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Email sent to {} (resendId={})", to, response.getId());
        } catch (ResendException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    private String getLoginUrl() {
        return System.getenv().getOrDefault("APP_LOGIN_URL", "http://localhost:4200/login");
    }
}
